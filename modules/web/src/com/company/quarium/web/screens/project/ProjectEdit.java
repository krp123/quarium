package com.company.quarium.web.screens.project;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.project.ConfigurationProjectRelationship;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.service.CopyChecklistService;
import com.company.quarium.web.screens.checklist.ExtChecklistEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@UiController("quarium_Project.edit")
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
    protected InstanceContainer<Project> projectDc;

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
    private UiComponents uiComponents;

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
        checklist = dataManager.load(Checklist.class).id(checklist.getId()).view("edit").one();
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

        checklistsTable.addGeneratedColumn("isUsedInRegress",
                new Table.ColumnGenerator<Checklist>() {
                    @Override
                    public Component generateCell(Checklist checklist) {
                        CheckBox checkBox = uiComponents.create(CheckBox.NAME);
                        checkBox.setWidth("100px");
                        if (checklist.getIsUsedInRegress()) {
                            boolean parentExists = false;
                            for (Checklist regress : regressChecklistDc.getMutableItems()) {
                                if (regress.getParentCard().equals(checklist)) {
                                    parentExists = true;
                                }
                            }
                            if (!parentExists) {
                                RegressChecklist checklistNew = copyChecklistService.copyChecklistToRegress(checklist);
                                regressChecklistDc.getMutableItems().add(checklistNew);
                            }
                        } else {
                            List<RegressChecklist> mutableItems = new ArrayList<>(regressChecklistDc.getMutableItems());
                            for (RegressChecklist cl : mutableItems) {
                                if (cl.getParentCard().equals(checklist)) {
                                    regressChecklistDc.getMutableItems().remove(cl);
                                }
                            }
                        }

                        checkBox.setValue(checklist.getIsUsedInRegress());

                        checkBox.addValueChangeListener(e -> {
                            checklist.setIsUsedInRegress(e.getValue());

                            if (e.getValue()) {
                                boolean parentExists = false;
                                for (Checklist regress : regressChecklistDc.getMutableItems()) {
                                    if (regress.getParentCard().equals(checklist)) {
                                        parentExists = true;
                                    }
                                }
                                if (!parentExists) {
                                    RegressChecklist checklistNew = copyChecklistService.copyChecklistToRegress(checklist);
                                    regressChecklistDc.getMutableItems().add(checklistNew);
                                }
                            } else {
                                        List<RegressChecklist> mutableItems = new ArrayList<>(regressChecklistDc.getMutableItems());
                                        for (RegressChecklist cl : mutableItems) {
                                            if (cl.getParentCard().equals(checklist)) {
                                                regressChecklistDc.getMutableItems().remove(cl);
                                                dataManager.remove(cl);
                                            }
                                        }
                                    }
                            //TODO один механизм использутся 2 раза. Придумать, как упростить
                                }
                        );


                        return checkBox;
                    }
                });
    }
}