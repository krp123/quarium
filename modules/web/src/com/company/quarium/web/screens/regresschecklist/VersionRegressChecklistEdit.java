package com.company.quarium.web.screens.regresschecklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.web.screens.checklist.VersionExtChecklistEdit;
import com.haulmont.cuba.gui.components.Link;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_VersionRegressChecklist.edit")
@UiDescriptor("version-regress-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class VersionRegressChecklistEdit extends VersionExtChecklistEdit {
    @Inject
    private InstanceContainer<Checklist> checklistDc;
    @Inject
    private Link ticket;
    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private Link caseTicket;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        ticket.setUrl(checklistDc.getItem().getTicket());
        caseTicket.setUrl(testCaseDc.getItem().getTicket());
    }
}