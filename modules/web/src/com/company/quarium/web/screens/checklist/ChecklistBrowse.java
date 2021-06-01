package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_Checklist.browse")
@UiDescriptor("checklist-browse.xml")
@LookupComponent("checklistsTable")
@LoadDataBeforeShow
public class ChecklistBrowse extends StandardLookup<Checklist> {
    @Inject
    private Screens screens;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionContainer<SimpleChecklist> checklistsDc;
    @Inject
    private GroupTable<SimpleChecklist> checklistsTable;
    @Inject
    private CollectionLoader<SimpleChecklist> checklistsDl;
    @Inject
    private Button refreshBtn;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Button uploadExcel;

    @Subscribe
    public void onInit(InitEvent event) {
        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            uploadExcel.setVisible(false);
        }
    }

    @Subscribe("uploadExcel")
    public void onUploadExcelClick(Button.ClickEvent event) {
        ExcelUploadWindow uploadWindow = screenBuilders.screen(this)
                .withScreenClass(ExcelUploadWindow.class)
                .build();
        uploadWindow.setChecklistsDc(checklistsDc);
        uploadWindow.setChecklistsDl(checklistsDl);
        uploadWindow.show();
    }
}