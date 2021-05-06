package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.checklist.TestCase;
import com.haulmont.cuba.gui.components.Link;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_TestCase.edit")
@UiDescriptor("test-case-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class TestCaseEdit extends StandardEditor<TestCase> {
    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private Link caseTicket;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        caseTicket.setUrl(testCaseDc.getItem().getTicket());
    }
}