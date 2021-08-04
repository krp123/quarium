package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("quarium_ProjectTestCaseBrowserFrame")
@UiDescriptor("testCase-browser-frame.xml")
public class ProjectTestCaseBrowserFrame extends ScreenFragment {
    @Inject
    private CollectionLoader<TestCase> testCasesDl;

    public void setProjectParameter(Project project) {
        testCasesDl.setParameter("project", project);
    }
}