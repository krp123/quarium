package com.company.quarium.web.screens.project;

import com.company.quarium.entity.project.SimpleProject;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_MyProjects.browse")
@UiDescriptor("my-projects-browse.xml")
@LookupComponent("projectsTable")
@LoadDataBeforeShow
public class MyProjectsBrowse extends ProjectBrowse {
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private CollectionLoader<SimpleProject> projectsDl;

    @Subscribe
    public void onInit(InitEvent event) {
        projectsDl.setParameter("currentUser", userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
    }
}