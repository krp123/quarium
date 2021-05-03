package com.company.quarium.web.screens.regresschecklist;

import com.company.quarium.web.screens.checklist.ExtChecklistEdit;
import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("quarium_RegressChecklist.edit")
@UiDescriptor("regress-checklist-edit.xml")
@EditedEntityContainer("regressChecklistDc")
@LoadDataBeforeShow
public class RegressChecklistEdit extends ExtChecklistEdit {
    @Override
    protected void initMasterDetailScreen(InitEvent event) {
        super.initMasterDetailScreen(event);
        isUsedInRegress.setVisible(false);
    }
}