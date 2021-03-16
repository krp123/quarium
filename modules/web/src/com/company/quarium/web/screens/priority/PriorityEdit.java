package com.company.quarium.web.screens.priority;

import com.company.quarium.entity.Priority;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Priority.edit")
@UiDescriptor("priority-edit.xml")
@EditedEntityContainer("priorityDc")
@LoadDataBeforeShow
public class PriorityEdit extends StandardEditor<Priority> {
}