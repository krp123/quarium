package com.company.quarium.web.screens.checklist;

import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("quarium_TestSuit.edit")
@UiDescriptor("testSuit-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class TestSuitEdit extends ExtChecklistEdit {
}