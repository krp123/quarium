package com.company.quarium.web.screens.simplechecklist;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_SimpleChecklist.browse")
@UiDescriptor("simple-checklist-browse.xml")
@LookupComponent("simpleChecklistsTable")
@LoadDataBeforeShow
public class SimpleChecklistBrowse extends StandardLookup<SimpleChecklist> {
}