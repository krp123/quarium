package com.company.quarium.web.screens.testrun;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.project.*;
import com.company.quarium.service.CopyChecklistService;
import com.company.quarium.web.screens.regresschecklist.RegressChecklistEdit;
import com.company.quarium.web.screens.simplechecklist.TestRunTestSuitBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.gui.actions.EditorPrintFormAction;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.company.quarium.Constants.STATE_CHECKED;

@UiController("quarium_TestRun.edit")
@UiDescriptor("test-run-edit.xml")
@EditedEntityContainer("testRunDc")
@LoadDataBeforeShow
public class TestRunEdit extends StandardEditor<TestRun> {

    @Inject
    private CollectionLoader<QaProjectRelationship> qaDl;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataManager dataManager;
    @Inject
    private CopyChecklistService copyChecklistService;
    @Inject
    private InstanceContainer<TestRun> testRunDc;
    @Inject
    private CollectionPropertyContainer<RegressChecklist> checklistsDc;
    @Inject
    private CollectionLoader<Milestone> milestoneDl;
    @Inject
    private Table<Module> modulesStatisticsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Table<QaProjectRelationship> testPlanQaStatisticsTable;
    @Inject
    private CollectionLoader<Module> moduleDl;
    @Inject
    private Button runReport;
    @Inject
    private DateField<LocalDateTime> runStartDate;
    @Inject
    private DateField<LocalDateTime> runFinishDate;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;

    @Subscribe("checklistTable.addChecklist")
    protected void onAddChecklist(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(SimpleChecklist.class, this)
                .withOptions(new MapScreenOptions(ParamsMap.of("project", getEditedEntity().getProject())))
                .withScreenClass(TestRunTestSuitBrowse.class)
                .withOpenMode(OpenMode.DIALOG)
                .withSelectHandler(checklists -> {
                    checklists.stream()
                            .forEach(this::createAndAddChecklist);
                })
                .build()
                .show();
    }

    private RegressChecklist createAndAddChecklist(Checklist checklist) {
        checklist = dataManager.load(Checklist.class).id(checklist.getId()).view("project-checklist-view").one();
        RegressChecklist checklistNew = copyChecklistService.copyRegressChecklist(checklist);
        checklistNew.setTestRun(testRunDc.getItem());
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    public void setProjectParameter(Project project) {
        qaDl.setParameter("project", project);
        milestoneDl.setParameter("project", project);
        moduleDl.setParameter("project", project);
    }

    @Install(to = "checklistTable.create", subject = "screenConfigurer")
    protected void testRunsTableCreateScreenConfigurer(Screen editorScreen) {
        ((RegressChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getProject().getModule());
        ((RegressChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getProject().getQa());
    }

    @Install(to = "checklistTable.edit", subject = "screenConfigurer")
    protected void testRunsTableEditScreenConfigurer(Screen editorScreen) {
        ((RegressChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getProject().getModule());
        ((RegressChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getProject().getQa());
    }

    private void countTime(Label label, List<Checklist> checklistList) {
        int totalTime = 0;
        for (Checklist cl : checklistList) {
            totalTime += cl.getHours() * 60 + cl.getMinutes();
        }

        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        label.setValue(hours + "ч " + minutes + "м");
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    public void onChecklistsDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<RegressChecklist> event) {
        if ("assignedQa".equals(event.getProperty())
                || "hours".equals(event.getProperty())
                || "minutes".equals(event.getProperty())
                || "module".equals(event.getProperty())) {
            repaintStatisticsTables();
        }
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    public void onChecklistsDcCollectionChange(CollectionContainer.CollectionChangeEvent<RegressChecklist> event) {
        repaintStatisticsTables();
    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        runReport.setAction(new EditorPrintFormAction(this, null));

        modulesStatisticsTable.addGeneratedColumn("timeTotal",
                new Table.ColumnGenerator<Module>() {
                    @Override
                    public Component generateCell(Module module) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> modules = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getModule() != null
                                            && s.getAssignedQa() != null)
                                        return s.getModule().equals(module);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        countTime(label, modules);
                        return label;
                    }
                });

        modulesStatisticsTable.addGeneratedColumn("timeLeft",
                new Table.ColumnGenerator<Module>() {
                    @Override
                    public Component generateCell(Module module) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> modules = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getModule() != null
                                            && s.getAssignedQa() != null)
                                        return s.getModule().equals(module);

                                    return false;
                                })
                                .filter(s -> {
                                    if (s.getState() != null)
                                        return !s.getState().getId().equals(STATE_CHECKED);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        countTime(label, modules);
                        return label;
                    }
                });

        modulesStatisticsTable.addGeneratedColumn("checklistsLeft",
                new Table.ColumnGenerator<Module>() {
                    @Override
                    public Component generateCell(Module module) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> modules = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getModule() != null && !s.getState().getId().equals(STATE_CHECKED)
                                            && s.getAssignedQa() != null)
                                        return s.getModule().equals(module);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        label.setValue(modules.size());
                        return label;
                    }
                });

        modulesStatisticsTable.addGeneratedColumn("checklistsTotal",
                new Table.ColumnGenerator<Module>() {
                    @Override
                    public Component generateCell(Module module) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> modules = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getModule() != null
                                            && s.getAssignedQa() != null)
                                        return s.getModule().equals(module);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        label.setValue(modules.size());
                        return label;
                    }
                });
        modulesStatisticsTable.addGeneratedColumn("completed",
                new Table.ColumnGenerator<Module>() {
                    @Override
                    public Component generateCell(Module module) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> modules = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getModule() != null
                                            && s.getAssignedQa() != null)
                                        return s.getModule().equals(module);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        List<Checklist> completed = modules.stream()
                                .filter(s -> {
                                    if (s.getState().getId().equals(STATE_CHECKED))
                                        return true;

                                    return false;
                                })
                                .collect(Collectors.toList());
                        double percent = 0.0;
                        if (modules.size() > 0) {
                            percent = completed.size() * 100 / (double) modules.size();
                        }

                        label.setValue(String.format("%.2f", percent) + "%");
                        return label;
                    }
                });

        testPlanQaStatisticsTable.addGeneratedColumn("timeTotal",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getAssignedQa() != null)
                                        return s.getAssignedQa().equals(qa);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        countTime(label, qaChecklists);
                        return label;
                    }
                });
        testPlanQaStatisticsTable.addGeneratedColumn("timeLeft",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getAssignedQa() != null)
                                        return s.getAssignedQa().equals(qa);

                                    return false;
                                })
                                .filter(s -> {
                                    if (s.getState() != null)
                                        return !s.getState().getId().equals(STATE_CHECKED);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        countTime(label, qaChecklists);
                        return label;
                    }
                });
    }

    private boolean checkRunDates() {
        if (runFinishDate.getValue() != null && runStartDate != null) {
            LocalDateTime start = runStartDate.getValue();
            LocalDateTime finish = runFinishDate.getValue();
            if (finish.isBefore(start)) {
                notifications.create(Notifications.NotificationType.TRAY)
                        .withDescription(String.format(messages.getMessage(getClass(), "testRunEdit.startDateValidation")))
                        .show();
                return false;
            } else return true;
        } else return true;
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        if (!checkRunDates()) {
            event.preventCommit();
        }
    }

    private void repaintStatisticsTables() {
        testPlanQaStatisticsTable.repaint();
        modulesStatisticsTable.repaint();
    }
}