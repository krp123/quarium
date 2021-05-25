package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_Checklist.browse")
@UiDescriptor("checklist-browse.xml")
@LookupComponent("checklistsTable")
@LoadDataBeforeShow
public class ChecklistBrowse extends StandardLookup<Checklist> {
    @Inject
    private Screens screens;

    @Subscribe("uploadExcel")
    public void onUploadExcelClick(Button.ClickEvent event) {
        screens.create(ExcelUploadWindow.class).show();
    }
}