package com.company.quarium.web.screens.testsuit;

import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("quarium_ProjectTestSuit.edit")
@UiDescriptor("projectTestSuit-edit.xml")
@EditedEntityContainer("testSuitDc")
@LoadDataBeforeShow
public class ProjectTestSuitEdit extends BaseTestSuitEdit {
}