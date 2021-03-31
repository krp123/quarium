package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.checklist.TestCase;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_TestCase.browse")
@UiDescriptor("test-case-browse.xml")
@LookupComponent("table")
@LoadDataBeforeShow
public class TestCaseBrowse extends MasterDetailScreen<TestCase> {
}