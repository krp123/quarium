package com.company.quarium.web.screens.priority;

import com.company.quarium.entity.Priority;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Priority.browse")
@UiDescriptor("priority-browse.xml")
@LookupComponent("prioritiesTable")
@LoadDataBeforeShow
public class PriorityBrowse extends StandardLookup<Priority> {
}