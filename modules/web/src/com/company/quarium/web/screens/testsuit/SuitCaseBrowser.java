package com.company.quarium.web.screens.testsuit;

import com.company.quarium.entity.project.Project;
import com.company.quarium.web.screens.testcase.ProjectTestCaseBrowserFrame;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("quarium_SuitCaseBrowser")
@UiDescriptor("suit-case-browser.xml")
public class SuitCaseBrowser extends Screen {
    @Inject
    private Fragments fragments;

    private Project project;

    public void setProjectParameter(Project project) {
        this.project = project;
    }

    @Subscribe
    public void onInit(InitEvent event) {
        ProjectTestSuitBrowserFrame suitBrowserFrame = fragments.create(this, ProjectTestSuitBrowserFrame.class);
        ProjectTestCaseBrowserFrame caseBrowserFrame = fragments.create(this, ProjectTestCaseBrowserFrame.class);
        suitBrowserFrame.setProjectParameter(project);
    }
}