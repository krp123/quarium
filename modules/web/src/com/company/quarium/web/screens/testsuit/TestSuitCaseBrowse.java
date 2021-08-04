package com.company.quarium.web.screens.testsuit;

import com.company.quarium.entity.testsuit.TestSuit;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_TestSuitCase.browse")
@UiDescriptor("test-suit-case-browse.xml")
@LookupComponent("testSuitsTable")
@LoadDataBeforeShow
public class TestSuitCaseBrowse extends StandardLookup<TestSuit> {

}