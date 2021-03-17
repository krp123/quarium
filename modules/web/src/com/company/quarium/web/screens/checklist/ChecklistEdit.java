package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Checklist.edit")
@UiDescriptor("checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class ChecklistEdit extends StandardEditor<Checklist> {
}