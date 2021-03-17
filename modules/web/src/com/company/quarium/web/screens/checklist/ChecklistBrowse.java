package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Checklist.browse")
@UiDescriptor("checklist-browse.xml")
@LookupComponent("checklistsTable")
@LoadDataBeforeShow
public class ChecklistBrowse extends StandardLookup<Checklist> {
}