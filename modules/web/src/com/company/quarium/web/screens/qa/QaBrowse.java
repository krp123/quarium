package com.company.quarium.web.screens.qa;

import com.company.quarium.entity.Qa;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Qa.browse")
@UiDescriptor("qa-browse.xml")
@LookupComponent("qasTable")
@LoadDataBeforeShow
public class QaBrowse extends StandardLookup<Qa> {
}