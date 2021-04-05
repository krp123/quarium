package com.company.quarium.web.screens.project;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.service.CopyChecklistService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

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
    protected InstanceContainer<Project> projectDc;

    @Inject
    private CollectionLoader<Checklist> checklistsDl;

    @Inject
    private CollectionContainer<Checklist> checklistsDc;

    @Inject
    private DataManager dataManager;

    @Inject
    private CopyChecklistService copyChecklistService;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        checklistsDl.setParameter("project", getEditedEntity());
    }

    @Subscribe("qaProjectRelationshipsTable.addQa")
    protected void onAddQa(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Qa.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(qas -> {
                    qas.stream()
                            .map(this::createRelationshipFromQa)
                            .forEach(this::addToRelationships);
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

    private Checklist createAndAddChecklist(Checklist checklist) {
        checklist = dataManager.load(Checklist.class).id(checklist.getId()).view("edit").one();
        Checklist checklistNew = copyChecklistService.copyChecklist(checklist, projectDc.getItem());
//            checklistNew.setProject(projectDc.getItem());
//            checklistNew.setName(checklist.getName());
//            if (checklist.getTestCase() != null) {
//                for (TestCase tc : checklist.getTestCase()) {
//                    TestCase newTC = dataManager.create(TestCase.class);
//                    newTC.setChecklist(checklistNew);
//                    newTC.setName(tc.getName());
//                    newTC.setStep(tc.getStep());
//                    newTC.setExpectedResult(tc.getExpectedResult());
//                    dataManager.commit(newTC);
//                }
//            }
//        dataManager.commit(checklistNew);
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    private void addToRelationships(QaProjectRelationship qaProjectRelationship) {
        qaProjectDc.getMutableItems().add(qaProjectRelationship);
    }
}