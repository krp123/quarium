package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testSuit.TestCase;
import com.haulmont.cuba.gui.components.Link;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_Defect.edit")
@UiDescriptor("defect-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class DefectEdit extends StandardEditor<TestCase> {
    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private Link caseTicket;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (testCaseDc.getItem().getTicket() != null) {
            caseTicket.setUrl(testCaseDc.getItem().getTicket());
            caseTicket.setCaption(testCaseDc.getItem().getTicket());
        } else
            caseTicket.setVisible(false);
    }
}