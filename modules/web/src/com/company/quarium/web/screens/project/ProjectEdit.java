package com.company.quarium.web.screens.project;

import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Qa;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.CollectionContainer;
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

    @Subscribe("qaProjectRelationshipsTable.addQa")
    protected void onAddInsuranceCompany(Action.ActionPerformedEvent event) {

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

    private QaProjectRelationship createRelationshipFromQa(Qa qa) {
        QaProjectRelationship qaProjectRelationship = dataContext.create(QaProjectRelationship.class);
        qaProjectRelationship.setProject(projectDc.getItem());
        qaProjectRelationship.setQa(qa);

        return qaProjectRelationship;
    }

    private void addToRelationships(QaProjectRelationship qaProjectRelationship) {
        qaProjectDc.getMutableItems().add(qaProjectRelationship);
    }
}