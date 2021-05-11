package com.company.quarium.web.screens.regresschecklist;

import com.company.quarium.web.screens.checklist.VersionExtChecklistEdit;
import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("quarium_VersionRegressChecklist.edit")
@UiDescriptor("version-regress-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class VersionRegressChecklistEdit extends VersionExtChecklistEdit {

}