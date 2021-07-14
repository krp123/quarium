package com.company.quarium.web.screens.project;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
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
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.Collection;

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
    private DataManager dataManager;
    @Inject
    private CopyChecklistService copyChecklistService;
    @Inject
    private CollectionLoader<QaProjectRelationship> qaDl;
    @Inject
    private TimeSource timeSource;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Button uploadExcel;
    @Inject
    private Button saveBtn;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Project> event) {
        event.getEntity().setCreationDate(timeSource.currentTimestamp());
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        qaDl.setParameter("project", getEditedEntity());
    }

    @Subscribe("qaProjectRelationshipsTable.addQa")
    protected void onAddQa(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Qa.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(qas -> {
                    qas.stream()
                            .filter(qa -> {
                                //если датасорс пустой, то добавляем всех
                                if (qaProjectDc.getItems().isEmpty()) {
                                    return true;
                                }

                                //проверяем, если хоть в одной из записей есть QA, то возвращаем false
                                boolean hasQa;
                                for (QaProjectRelationship qpr : qaProjectDc.getItems()) {
                                    hasQa = qpr.getQa().equals(qa);
                                    if (hasQa)
                                        return false;
                                }
                                return true;
                            })
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

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        saveBtn.setAction(new AbstractAction("saveCard") {
            @Override
            public void actionPerform(Component component) {
                commitChanges();
                notifications.create()
                        .withCaption(messages.getMessage(getClass(), "saveNotification"))
                        .show();
            }
        });

        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            uploadExcel.setVisible(false);
            saveBtn.setVisible(false);
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