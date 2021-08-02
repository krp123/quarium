package com.company.quarium.web.screens.testrun;

import com.company.quarium.entity.project.*;
import com.company.quarium.entity.testSuit.RunTestSuit;
import com.company.quarium.entity.testSuit.SharedTestSuit;
import com.company.quarium.entity.testSuit.TestCase;
import com.company.quarium.entity.testSuit.TestSuit;
import com.company.quarium.service.CopyTestSuitService;
import com.company.quarium.web.screens.runtestsuit.RunTestSuitEdit;
import com.company.quarium.web.screens.simplechecklist.TestRunTestSuitBrowse;
import com.company.quarium.web.screens.testSuit.SuitCaseBrowser;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.charts.gui.components.charts.PieChart;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
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
import java.time.LocalDate;
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
    private CopyTestSuitService copyTestSuitService;
    @Inject
    private InstanceContainer<TestRun> testRunDc;
    @Inject
    private CollectionPropertyContainer<RunTestSuit> checklistsDc;
    @Inject
    private CollectionLoader<Milestone> milestoneDl;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Table<QaProjectRelationship> testPlanQaStatisticsTable;
    @Inject
    private CollectionLoader<Module> moduleDl;
    @Inject
    private Button runReport;
    @Inject
    private DateField<LocalDate> runStartDate;
    @Inject
    private DateField<LocalDate> runFinishDate;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;
    @Inject
    private CollectionLoader<TestCase> bugsDl;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private LookupField<Milestone> milestone;
    @Inject
    private CollectionLoader<RunTestSuit> suitsDl;
    @Inject
    private GroupTable<RunTestSuit> suitsStatisticsTable;
    @Inject
    private CollectionContainer<TestCase> skippedCasesDc;
    @Inject
    private CollectionContainer<TestCase> blockedCasesDc;
    @Inject
    private CollectionContainer<TestCase> passedCasesDc;
    @Inject
    private CollectionContainer<TestCase> bugsDc;
    @Inject
    private CollectionContainer<TestCase> totalCasesDc;
    @Inject
    private CollectionLoader<TestCase> totalCasesDl;
    @Inject
    private CollectionLoader<TestCase> passedCasesDl;
    @Inject
    private CollectionLoader<TestCase> blockedCasesDl;
    @Inject
    private CollectionLoader<TestCase> skippedCasesDl;
    @Inject
    private CollectionLoader<RunTestSuit> checklistsFilterDl;
    @Inject
    private GroupTable<RunTestSuit> checklistTable;
    @Inject
    private PieChart pieChart;

    @Subscribe("checklistTable.addChecklist")
    protected void onAddChecklist(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(SharedTestSuit.class, this)
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

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        bugsDl.setParameter("testRun", getEditedEntity());
        suitsDl.setParameter("testRun", getEditedEntity());
        totalCasesDl.setParameter("testRun", getEditedEntity());
        passedCasesDl.setParameter("testRun", getEditedEntity());
        blockedCasesDl.setParameter("testRun", getEditedEntity());
        skippedCasesDl.setParameter("testRun", getEditedEntity());
        checklistsFilterDl.setParameter("testRun", getEditedEntity());

    }

    private RunTestSuit createAndAddChecklist(TestSuit testSuit) {
        testSuit = dataManager.load(TestSuit.class).id(testSuit.getId()).view("project-testSuit-view").one();
        RunTestSuit checklistNew = copyTestSuitService.copyRunTestSuit(testSuit);
        checklistNew.setTestRun(testRunDc.getItem());
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    public void setProjectParameter(Project project) {
        qaDl.setParameter("project", project);
        milestoneDl.setParameter("project", project);
        moduleDl.setParameter("project", project);
    }

    @Install(to = "checklistTable.addChecklist", subject = "screenConfigurer")
    private void checklistTableAddChecklistScreenConfigurer(Screen screen) {
        ((SuitCaseBrowser) screen).setProjectParameter(getEditedEntity().getProject());
    }

    @Install(to = "checklistTable.create", subject = "screenConfigurer")
    protected void testRunsTableCreateScreenConfigurer(Screen editorScreen) {
        ((RunTestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getProject().getModule());
        ((RunTestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getProject().getQa());
    }

    @Install(to = "checklistTable.edit", subject = "screenConfigurer")
    protected void testRunsTableEditScreenConfigurer(Screen editorScreen) {
        ((RunTestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getProject().getModule());
        ((RunTestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getProject().getQa());
    }

    private String getTime(List<TestSuit> testSuitList) {
        int totalTime = 0;
        for (TestSuit cl : testSuitList) {
            totalTime += cl.getHours() * 60 + cl.getMinutes();
        }

        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        return hours + "ч " + minutes + "м";
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    public void onChecklistsDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<RunTestSuit> event) {
        if ("assignedQa".equals(event.getProperty())
                || "hours".equals(event.getProperty())
                || "minutes".equals(event.getProperty())
                || "module".equals(event.getProperty())) {
            repaintStatisticsTables();
        }
    }

    @Subscribe(id = "totalCasesDc", target = Target.DATA_CONTAINER)
    public void onTotalCasesDcCollectionChange(CollectionContainer.CollectionChangeEvent<TestCase> event) {
        repaintStatisticsTables();
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    public void onChecklistsDcCollectionChange(CollectionContainer.CollectionChangeEvent<RunTestSuit> event) {
        repaintStatisticsTables();
    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        runReport.setAction(new EditorPrintFormAction(this, messages.getMessage(getClass(), "testRunEdit.testRunReport")));

        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            milestone.setEditable(false); //Костыль. Почему то не удаляется доступ к редактированию поля через роль.
        }

        suitsStatisticsTable.addGeneratedColumn("casesSkipped",
                new Table.ColumnGenerator<TestSuit>() {
                    @Override
                    public Component generateCell(TestSuit testSuit) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestCase> skippedCases = skippedCasesDc.getMutableItems().stream()
                                .filter(s ->
                                        s.getTestSuit().equals(testSuit))
                                .collect(Collectors.toList());
                        label.setValue(skippedCases.size());
                        label.setStyleName("skipped-result");
                        return label;
                    }
                });

        suitsStatisticsTable.addGeneratedColumn("casesBlocked",
                new Table.ColumnGenerator<TestSuit>() {
                    @Override
                    public Component generateCell(TestSuit testSuit) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestCase> blockedCases = blockedCasesDc.getMutableItems().stream()
                                .filter(s ->
                                        s.getTestSuit().equals(testSuit))
                                .collect(Collectors.toList());
                        label.setValue(blockedCases.size());
                        label.setStyleName("blocked-result");
                        return label;
                    }
                });

        suitsStatisticsTable.addGeneratedColumn("casesFailed",
                new Table.ColumnGenerator<TestSuit>() {
                    @Override
                    public Component generateCell(TestSuit testSuit) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestCase> failedCases = bugsDc.getMutableItems().stream()
                                .filter(s ->
                                        s.getTestSuit().equals(testSuit))
                                .collect(Collectors.toList());
                        label.setValue(failedCases.size());
                        label.setStyleName("failed-result");
                        return label;
                    }
                });

        suitsStatisticsTable.addGeneratedColumn("casesPassed",
                new Table.ColumnGenerator<TestSuit>() {
                    @Override
                    public Component generateCell(TestSuit testSuit) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestCase> passedCases = passedCasesDc.getMutableItems().stream()
                                .filter(s ->
                                        s.getTestSuit().equals(testSuit))
                                .collect(Collectors.toList());
                        label.setValue(passedCases.size());
                        label.setStyleName("passed-result");
                        return label;
                    }
                });

        suitsStatisticsTable.addGeneratedColumn("casesTotal",
                new Table.ColumnGenerator<TestSuit>() {
                    @Override
                    public Component generateCell(TestSuit testSuit) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestCase> totalCases = totalCasesDc.getMutableItems().stream()
                                .filter(s ->
                                        s.getTestSuit().equals(testSuit))
                                .collect(Collectors.toList());
                        label.setValue(totalCases.size());
                        return label;
                    }
                });

        testPlanQaStatisticsTable.addGeneratedColumn("timeTotal",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestSuit> qaTestSuits = checklistsDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getAssignedQa() != null)
                                        return s.getAssignedQa().equals(qa);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        label.setValue(getTime(qaTestSuits));
                        return label;
                    }
                });

        testPlanQaStatisticsTable.addGeneratedColumn("timeLeft",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<TestSuit> qaTestSuits = checklistsDc.getMutableItems().stream()
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
                        label.setValue(getTime(qaTestSuits));
                        return label;
                    }
                });
    }

    private boolean checkRunDates() {
        if (runFinishDate.getValue() != null && runStartDate != null) {
            LocalDate start = runStartDate.getValue();
            LocalDate finish = runFinishDate.getValue();
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
        suitsStatisticsTable.repaint();
    }

    @Subscribe("createPopup.copy")
    public void onCreatePopupCopy(Action.ActionPerformedEvent event) {
        if (checklistTable.getSingleSelected() != null) {
            RunTestSuit newCl = copyTestSuitService.copyRunTestSuit(checklistTable.getSingleSelected());
            newCl.setTestRun(getEditedEntity());
            checklistsDc.getMutableItems().add(newCl);

            screenBuilders.editor(checklistTable)
                    .editEntity(newCl)
                    .build()
                    .show();
        }
    }
}