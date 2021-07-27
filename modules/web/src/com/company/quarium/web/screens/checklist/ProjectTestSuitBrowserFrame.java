package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.project.Project;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("quarium_ProjectTestSuitBrowserFrame")
@UiDescriptor("project-testSuit-browser-frame.xml")
public class ProjectTestSuitBrowserFrame extends ScreenFragment {
    @Inject
    private CollectionLoader<Checklist> testSuitsDl;

    public void setProjectParameter(Project project) {
        testSuitsDl.setParameter("project", project);
    }
}