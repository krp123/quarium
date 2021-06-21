package com.company.quarium.web.screens.project;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.ConfigurationProjectRelationship;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.project.SimpleProject;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.service.CopyChecklistService;
import com.company.quarium.web.screens.checklist.ProjectExcelUploadWindow;
import com.company.quarium.web.screens.checklist.TestSuitEdit;
import com.company.quarium.web.screens.testrun.TestRunEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UiController("quarium_SimpleProject.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
@LoadDataBeforeShow
public class ProjectEdit extends StandardEditor<Project> {

    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected DataContext dataContext;
    @Inject
    protected CollectionContainer<QaProjectRelationship> qaProjectDc;
    @Inject
    protected CollectionContainer<ConfigurationProjectRelationship> configurationProjectDc;
    @Inject
    protected InstanceContainer<SimpleProject> projectDc;
    @Inject
    private CollectionContainer<SimpleChecklist> checklistsDc;
    @Inject
    private CollectionContainer<RegressChecklist> regressChecklistDc;
    @Inject
    private DataManager dataManager;
    @Inject
    private CopyChecklistService copyChecklistService;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private CollectionLoader<TestCase> bugsDl;
    @Inject
    private CollectionLoader<QaProjectRelationship> qaDl;
    @Inject
    private TimeSource timeSource;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Button uploadExcel;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Project> event) {
        event.getEntity().setCreationDate(timeSource.currentTimestamp());
    }

/*    @Subscribe("addNewVersion")
    public void onAddNewVersionClick(Button.ClickEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Внимание")
                .withMessage("Данная версия проекта будет перемещена на вкладку Релизы. Часть полей будет очищена." +
                        " Вы уверены?")
                .withActions(
                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(d -> {
                            ProjectVersion newVersion = copyChecklistService.copyProjectToReleases(getEditedEntity());
                            versionsDc.getMutableItems().add(newVersion);
                            projectDc.getItem().setCurrentRelease(null);
                            projectDc.getItem().setRegressStartDate(null);
                            projectDc.getItem().setRegressFinishDate(null);
                            projectDc.getItem().getRegressChecklist().clear();
                            projectDc.getItem().setCreationDate(timeSource.currentTimestamp());

                            for (Checklist cl : checklistsDc.getMutableItems()) {
                                cl.setState(dataContext.find(Statement.class, STATE_NOT_STARTED));
                                cl.setIsUsedInRegress(false);
                                cl.setAssignedQa(null);
                                for (TestCase tc : cl.getTestCase()) {
                                    tc.setState(dataContext.find(Statement.class, STATE_NOT_STARTED));
                                    tc.setTicket(null);
                                    tc.setComment(null);
                                }
                            }
                        }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();

    }*/

//    @Install(to = "regressChecklistsTable.remove", subject = "afterActionPerformedHandler")
//    private void regressChecklistsTableRemoveAfterActionPerformedHandler(RemoveOperation.AfterActionPerformedEvent<RegressChecklist> afterActionPerformedEvent) {
//        for (Checklist cl : afterActionPerformedEvent.getItems()) {
//            if (cl.getParentCard() != null) {
//                cl.getParentCard().setIsUsedInRegress(false);
//            }
//        }
//    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        bugsDl.setParameter("project", getEditedEntity());
        qaDl.setParameter("project", getEditedEntity());
    }

    @Subscribe("qaProjectRelationshipsTable.addQa")
    protected void onAddQa(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Qa.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(qas -> {
                    qas.stream()
                            .map(this::createRelationshipFromQa)
                            .forEach(this::addQaToRelationships);
                })
                .build()
                .show();
    }


    @Subscribe("configurationProjectRelationshipsTable.addConfiguration")
    protected void onAddConfiguration(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Configuration.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(configurations -> {
                    configurations.stream()
                            .map(this::createRelationshipFromConfiguration)
                            .forEach(this::addConfigurationToRelationships);
                })
                .build()
                .show();
    }

    @Subscribe("checklistsTable.addChecklist")
    protected void onAddChecklist(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Checklist.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(checklists -> {
                    checklists.stream()
                            .forEach(this::createAndAddChecklist);
                })
                .build()
                .show();
    }

    private QaProjectRelationship createRelationshipFromQa(Qa qa) {
        QaProjectRelationship qaProjectRelationship = dataContext.create(QaProjectRelationship.class);
        qaProjectRelationship.setProject(projectDc.getItem());
        qaProjectRelationship.setQa(qa);

        return qaProjectRelationship;
    }

    private ConfigurationProjectRelationship createRelationshipFromConfiguration(Configuration configuration) {
        ConfigurationProjectRelationship configurationProjectRelationship =
                dataContext.create(ConfigurationProjectRelationship.class);
        configurationProjectRelationship.setProject(projectDc.getItem());
        configurationProjectRelationship.setConfiguration(configuration);

        return configurationProjectRelationship;
    }

    private Checklist createAndAddChecklist(Checklist checklist) {
        checklist = dataManager.load(Checklist.class).id(checklist.getId()).view("project-checklist-view").one();
        SimpleChecklist checklistNew = copyChecklistService.copyChecklist(checklist);
        checklistNew.setProject(projectDc.getItem());
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    private void addQaToRelationships(QaProjectRelationship qaProjectRelationship) {
        qaProjectDc.getMutableItems().add(qaProjectRelationship);
    }

    private void addConfigurationToRelationships(ConfigurationProjectRelationship configurationProjectRelationship) {
        configurationProjectDc.getMutableItems().add(configurationProjectRelationship);
    }

    @Install(to = "testRunsTable.create", subject = "screenConfigurer")
    protected void testRunsTableCreateScreenConfigurer(Screen editorScreen) {
        ((TestRunEdit) editorScreen).setProjectParameter(getEditedEntity());
    }

    @Install(to = "testRunsTable.edit", subject = "screenConfigurer")
    protected void testRunsTableEditScreenConfigurer(Screen editorScreen) {
        ((TestRunEdit) editorScreen).setProjectParameter(getEditedEntity());
    }

    @Install(to = "checklistsTable.edit", subject = "screenConfigurer")
    protected void checklistTableEditScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((TestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((TestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

    @Install(to = "checklistsTable.create", subject = "screenConfigurer")
    protected void checklistTableCreateScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((TestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((TestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

//    @Install(to = "regressChecklistsTable.edit", subject = "screenConfigurer")
//    protected void regressChecklistTableEditScreenConfigurer(Screen editorScreen) {
//        if (getEditedEntity().getQa() != null) {
//            ((ExtChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
//        }
//
//        if (getEditedEntity().getModule() != null) {
//            ((ExtChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
//        }
//    }
//
//    @Install(to = "regressChecklistsTable.create", subject = "screenConfigurer")
//    protected void regressChecklistTableCreateScreenConfigurer(Screen editorScreen) {
//        if (getEditedEntity().getQa() != null) {
//            ((ExtChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
//        }
//
//        if (getEditedEntity().getModule() != null) {
//            ((ExtChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
//        }
//    }

/*    @Subscribe("checklistsTable.isUsedInRegress")
    public void onChecklistsTableIsUsedInRegressClick(Table.Column.ClickEvent<SimpleChecklist> event) {
        Checklist checklist = checklistsDc.getItem(event.getItem());
        if (checklist.getIsUsedInRegress()) {
            dialogs.createOptionDialog()
                    .withCaption("Внимание")
                    .withMessage("Снятие атрибута повлечет за собой удаление чек-листа с вкладки Регресс. " +
                            "Вы уверены?")
                    .withActions(
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(d -> {
                                checklist.setIsUsedInRegress(false);
                                removeRegressChecklist(checklist);
                            }),
                            new DialogAction(DialogAction.Type.NO).withHandler(d -> {
                                checklist.setIsUsedInRegress(true);
                            })
                    )
                    .show();
        }
    }*/

//    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
//    public void onChecklistsDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<SimpleChecklist> event) {
//        if (!"isUsedInRegress".equals(event.getProperty())) {
//            return;
//        }
//
//        Checklist checklist = checklistsDc.getItem(event.getItem());
//
//        //если установили галку
//        if (BooleanUtils.isTrue(checklist.getIsUsedInRegress())) {
//            boolean parentExists = false;
//            //Проверяем, является ли текущий чек-лист для какого-то чек-листа регресса родителем
//            for (Checklist regress : regressChecklistDc.getMutableItems()) {
//                if (regress.getParentCard() != null &&
//                        regress.getParentCard().equals(checklist)) {
//                    parentExists = true;
//                    List<TestCase> fromParent = new ArrayList<>();
//                    //Собираем тест-кейсы с приоритетом "Высокий"
//                    if (regress.getParentCard() != null &&
//                            regress.getParentCard().getTestCase() != null) {
//                        fromParent = regress.getParentCard().getTestCase().stream().filter(s ->
//                                s.getPriority().getId().equals(PRIORITY_HIGH)).collect(Collectors.toList());
//                    }
//                    List<TestCase> fromRegress = regress.getTestCase();
//                    for (TestCase tcParent : fromParent) {
//                        boolean listHasCase = false;
//                        //Проверяем, имеет ли уже чек-лист такой тест-кейс
//                        for (TestCase tcRegress : fromRegress) {
//                            if (tcParent.getCreationDate() != null &&
//                                    tcParent.getCreationDate().equals(tcRegress.getCreationDate()))
//                                listHasCase = true;
//                        }
//                        //если такого кейса нет, то копируем его
//                        if (!listHasCase) {
//                            regress.setTestCase(copyChecklistService.copyTestCaseToChecklist(regress, tcParent));
//                        }
//                    }
//                }
//            }
//            //если родителя нет, то сразу копируем полностью чек-лист
//            if (!parentExists) {
//                copyChecklistToRegress(checklist);
//            }
//        } else {
//            //если сняли галку
//            boolean hasChild = false;
//            //проверяем, есть ли данный чек-лист на вкладке "Регресс"
//            for (RegressChecklist cl : regressChecklistDc.getMutableItems()) {
//                if (cl.getParentCard() != null &&
//                        cl.getParentCard().equals(checklist)) {
//                    hasChild = true;
//                }
//            }
//            //если есть, то выводим диалоговое окно
//            if (hasChild) {
////                if ("testPlanTab".equals(projectTabSheet.getSelectedTab().getName())) { //TODO баг: открыть вкладку "Тест-план", нажать на кнопку "Новый релиз". Появится диалоговое окно. Мб здесь сделать лиснер клика по чек-боксу?
////
////                }
////            } else {
//                removeRegressChecklist(checklist);
//            }
//        }
//    }

    private void countTime(Label label, List<Checklist> checklistList) {
        int totalTime = 0;
        for (Checklist cl : checklistList) {
            totalTime += cl.getHours() * 60 + cl.getMinutes();
        }

        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        label.setValue(hours + "ч " + minutes + "м");
    }

//    @Subscribe
//    public void onAfterShow(AfterShowEvent event) {
//        checklistsTable.getColumn("isUsedInRegress").addClickListener(e -> {
//            Checklist checklist = checklistsTable.getSingleSelected();
//            if (!checklist.getIsUsedInRegress()) {
//                dialogs.createOptionDialog()
//                        .withCaption("Внимание")
//                        .withMessage("Снятие атрибута повлечет за собой удаление чек-листа с вкладки Регресс. " +
//                                "Вы уверены?")
//                        .withActions(
//                                new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(d -> {
//                                    checklist.setIsUsedInRegress(false);
//                                    removeRegressChecklist(checklist);
//                                }),
//                                new DialogAction(DialogAction.Type.NO).withHandler(d -> {
//                                    checklist.setIsUsedInRegress(true);
//                                })
//                        )
//                        .show();
//            }
//        });
//    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            uploadExcel.setVisible(false);
        }

//        runReport.setAction(new EditorPrintFormAction(this, null));

/*        modulesStatisticsTable.addGeneratedColumn("timeTotal",
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
                });*/
/*
        qaStatisticsTable.addGeneratedColumn("timeTotal",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = regressChecklistDc.getMutableItems().stream()
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

        qaStatisticsTable.addGeneratedColumn("timeLeft",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = regressChecklistDc.getMutableItems().stream()
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
                });*/
    }

    private void copyChecklistToRegress(Checklist checklist) {
        RegressChecklist checklistNew = copyChecklistService.copyChecklistToRegress(checklist);
        regressChecklistDc.getMutableItems().add(checklistNew);
    }

    private void removeRegressChecklist(Checklist checklist) {
        List<RegressChecklist> mutableItems = new ArrayList<>(regressChecklistDc.getMutableItems());
        for (RegressChecklist cl : mutableItems) {
            if (cl.getParentCard() != null &&
                    cl.getParentCard().equals(checklist)) {
                regressChecklistDc.getMutableItems().remove(cl);
            }
        }
    }

    @Subscribe("uploadExcel")
    public void onUploadExcelClick(Button.ClickEvent event) {
        ProjectExcelUploadWindow uploadWindow = screenBuilders.screen(this)
                .withScreenClass(ProjectExcelUploadWindow.class)
                .build();
        uploadWindow.setChecklistsDc(checklistsDc);
        uploadWindow.setProject((SimpleProject) getEditedEntity());
        uploadWindow.show();
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    private void onChecklistsDcCollectionChange(
            CollectionContainer.CollectionChangeEvent<SimpleChecklist> event) {
        CollectionChangeType changeType = event.getChangeType();
        Collection<? extends SimpleChecklist> changes = event.getChanges();
    }
}