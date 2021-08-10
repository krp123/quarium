package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("quarium_RunTestCaseBrowserFrame")
@UiDescriptor("run-testCase-browser-frame.xml")
public class RunTestCaseBrowserFrame extends ScreenFragment {
    Project project;
    @Inject
    private CollectionLoader<TestCase> testCasesDl;

    @Subscribe
    public void onInit(InitEvent event) {
        testCasesDl.setParameter("project", project);
        testCasesDl.load();
    }

    public void setProjectParameter(Project project) {
        this.project = project;
    }
}