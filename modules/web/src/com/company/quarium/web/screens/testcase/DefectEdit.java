package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Defect.edit")
@UiDescriptor("defect-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class DefectEdit extends StandardEditor<TestCase> {
}