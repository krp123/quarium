package com.company.quarium.web.screens.project;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.*;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.entity.references.Statement;
import com.company.quarium.service.CopyChecklistService;
import com.company.quarium.web.screens.checklist.ExtChecklistEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private Table<SimpleChecklist> checklistsTable;
    @Inject
    private GroupTable<RegressChecklist> regressChecklistsTable;
    @Inject
    private Table<QaProjectRelationship> qaStatisticsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private CollectionLoader<TestCase> bugsDl;
    @Inject
    private Dialogs dialogs;
    @Inject
    private Button addNewVersion;
    @Inject
    private CollectionPropertyContainer<ProjectVersion> versionsDc;
    @Inject
    private TimeSource timeSource;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Project> event) {
        event.getEntity().setCreationDate(timeSource.currentTimestamp());
    }

    @Subscribe("addNewVersion")
    public void onAddNewVersionClick(Button.ClickEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Внимание")
                .withMessage("Данная версия проекта будет перемещена на вкладку Релизы. Все поля будут обнулены." +
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
                                cl.setState(dataContext.find(Statement.class, UUID.fromString("31c599f1-c1b0-30ae-add1-5c6e4b354276")));
                                cl.setIsUsedInRegress(false);
                                cl.setAssignedQa(null);
                                for (TestCase tc : cl.getTestCase()) {
                                    tc.setState(dataContext.find(Statement.class, UUID.fromString("31c599f1-c1b0-30ae-add1-5c6e4b354276")));
                                    tc.setTicket(null);
                                    tc.setComment(null);
                                }
                            }
                        }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();

    }

    @Subscribe("regressChecklistsTable.remove")
    public void onRegressChecklistsTableRemove(Action.ActionPerformedEvent event) {
        dialogs.createOptionDialog()
                .withCaption("Подтверждение")
                .withMessage("Вы действительно хотите удалить выбранные элементы?")
                .withActions(
                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(d -> {
                            if (regressChecklistsTable.getSingleSelected().getParentCard() != null)
                                checklistsDc.getItem(regressChecklistsTable.getSingleSelected().getParentCard().getId())
                                        .setIsUsedInRegress(false);
                            regressChecklistDc.getMutableItems().remove(regressChecklistsTable.getSingleSelected());
                        }),
                        new DialogAction(DialogAction.Type.NO).withHandler(d -> {
                        })
                )
                .show();
    }


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        bugsDl.setParameter("project", getEditedEntity());
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
        SimpleChecklist checklistNew = copyChecklistService.copyChecklist(checklist, projectDc.getItem());
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    private void addQaToRelationships(QaProjectRelationship qaProjectRelationship) {
        qaProjectDc.getMutableItems().add(qaProjectRelationship);
    }

    private void addConfigurationToRelationships(ConfigurationProjectRelationship configurationProjectRelationship) {
        configurationProjectDc.getMutableItems().add(configurationProjectRelationship);
    }

    @Install(to = "checklistsTable.edit", subject = "screenConfigurer")
    protected void customersTableEditScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((ExtChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((ExtChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

    @Install(to = "checklistsTable.create", subject = "screenConfigurer")
    protected void customersTableCreateScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((ExtChecklistEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((ExtChecklistEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        List<QaProjectRelationship> qaList = getEditedEntity().getQa();

        checklistsTable.addGeneratedColumn("assignedQa",
                new Table.ColumnGenerator<Checklist>() {
                    @Override
                    public Component generateCell(Checklist checklist) {
                        LookupField<QaProjectRelationship> lookupField = uiComponents.create(LookupField.NAME);
                        if (qaList != null) {
                            lookupField.setOptionsList(qaList);
                            lookupField.setWidth("100px");

                            lookupField.setValue(checklist.getAssignedQa());
                            lookupField.addValueChangeListener(e -> {
                                        checklist.setAssignedQa(e.getValue());
                                    }
                            );
                        }
                        return lookupField;
                    }
                });

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
                        int totalTime = 0;
                        for (Checklist cl : qaChecklists) {
                            totalTime += cl.getHours() * 60 + cl.getMinutes();
                        }

                        int hours = totalTime / 60;
                        int minutes = totalTime % 60;
                        label.setValue(hours + "ч " + minutes + "м");
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
                                        return !s.getState().getId().toString().equals("d9d8fd34-068d-99db-5adc-9d95731bc419");

                                    return false;
                                })
                                .collect(Collectors.toList());
                        int totalTime = 0;
                        for (Checklist cl : qaChecklists) {
                            totalTime += cl.getHours() * 60 + cl.getMinutes();
                        }

                        int hours = totalTime / 60;
                        int minutes = totalTime % 60;
                        label.setValue(hours + "ч " + minutes + "м");
                        return label;
                    }
                });

        checklistsTable.addGeneratedColumn("isUsedInRegress",
                new Table.ColumnGenerator<Checklist>() {
                    @Override
                    public Component generateCell(Checklist checklist) {
                        CheckBox checkBox = uiComponents.create(CheckBox.NAME);
                        checkBox.setWidth("100px");

                        checkBox.setValue(checklist.getIsUsedInRegress());

                        //Копирование чек-листа на вкладку "Регресс"
                        if (checklist.getIsUsedInRegress() != null
                                && checklist.getIsUsedInRegress()) {
                            boolean parentExists = false;
                            //Проверяем, является ли текущий чек-лист для какого-то чек-листа регресса родителем
                            for (Checklist regress : regressChecklistDc.getMutableItems()) {
                                if (regress.getParentCard() != null &&
                                        regress.getParentCard().equals(checklist)) {
                                    parentExists = true;
                                    List<TestCase> fromParent = new ArrayList<>();
                                    //Собираем тест-кейсы с приоритетом "Высокий"
                                    if (regress.getParentCard() != null) {
                                        fromParent = regress.getParentCard().getTestCase().stream().filter(s ->
                                                s.getPriority().getId().toString().equals("e2e009c7-4f9c-be4a-6b0e-a9d7c9db7dd0")).collect(Collectors.toList());
                                    }
                                    List<TestCase> fromRegress = regress.getTestCase();
                                    for (TestCase tcParent : fromParent) {
                                        boolean listHasCase = false;
                                        //Проверяем, имеет ли уже чек-лист такой тест-кейс
                                        for (TestCase tcRegress : fromRegress) {
                                            if (tcParent.getCreationDate() != null &&
                                                    tcParent.getCreationDate().equals(tcRegress.getCreationDate()))
                                                listHasCase = true;
                                        }
                                        //если такого кейса нет, то копируем его
                                        if (!listHasCase) {
                                            regress.setTestCase(copyChecklistService.copyTestCaseToChecklist(regress, tcParent));
                                        }
                                    }
                                }
                            }
                            //если родителя нет, то сразу копируем полностью чек-лист
                            if (!parentExists) {
                                copyChecklistToRegress(checklist);
                            }
                        } else {
                            removeChecklist(checklist);
                        }

                        checkBox.addValueChangeListener(e -> {

                                    if (e.getValue()) {
                                        checklist.setIsUsedInRegress(e.getValue());
                                        boolean parentExists = false;
                                        for (Checklist regress : regressChecklistDc.getMutableItems()) {
                                            if (regress.getParentCard().equals(checklist)) {
                                                parentExists = true;
                                            }
                                        }
                                        if (!parentExists) {
                                            copyChecklistToRegress(checklist);
                                        }
                                    } else {
                                        dialogs.createOptionDialog()
                                                .withCaption("Внимание")
                                                .withMessage("Снятие атрибута повлечет за собой удаление чек-листа с вкладки Регресс. " +
                                                        "Вы уверены?")
                                                .withActions(
                                                        new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(d -> {
                                                            checklist.setIsUsedInRegress(e.getValue());
                                                            removeChecklist(checklist);
                                                        }),
                                                        new DialogAction(DialogAction.Type.NO).withHandler(d -> {
                                                            checkBox.setValue(true);
                                                        })
                                                )
                                                .show();
                                    }

                                }
                        );
                        return checkBox;
                    }

                });
    }

    private void copyChecklistToRegress(Checklist checklist) {
        RegressChecklist checklistNew = copyChecklistService.copyChecklistToRegress(checklist);
        regressChecklistDc.getMutableItems().add(checklistNew);
    }

    private void removeChecklist(Checklist checklist) {
        List<RegressChecklist> mutableItems = new ArrayList<>(regressChecklistDc.getMutableItems());
        for (RegressChecklist cl : mutableItems) {
            if (cl.getParentCard().equals(checklist)) {
                regressChecklistDc.getMutableItems().remove(cl);
                dataManager.remove(cl);
            }
        }
    }
}