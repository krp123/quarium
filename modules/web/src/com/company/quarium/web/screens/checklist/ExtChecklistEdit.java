package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.haulmont.cuba.gui.screen.*;

@UiController("ext_quarium_Checklist.edit")
@UiDescriptor("ext-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class ExtChecklistEdit extends StandardEditor<Checklist> {
}