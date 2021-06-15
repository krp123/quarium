package com.company.quarium.web.screens.testrun;

import com.company.quarium.entity.project.TestRun;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_TestRun.edit")
@UiDescriptor("test-run-edit.xml")
@EditedEntityContainer("testRunDc")
@LoadDataBeforeShow
public class TestRunEdit extends StandardEditor<TestRun> {
}