package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.service.CopyChecklistService;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.PopupButton;
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
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionContainer<SimpleChecklist> checklistsDc;
    @Inject
    private CollectionLoader<SimpleChecklist> checklistsDl;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private PopupButton createPopup;
    @Inject
    private GroupTable<SimpleChecklist> checklistsTable;
    @Inject
    private CopyChecklistService copyChecklistService;

    @Subscribe
    public void onInit(InitEvent event) {
        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            createPopup.setVisible(false);
        }
    }

    @Subscribe("createPopup.uploadExcel")
    public void onCreatePopupUploadExcel(Action.ActionPerformedEvent event) {
        ExcelUploadWindow uploadWindow = screenBuilders.screen(this)
                .withScreenClass(ExcelUploadWindow.class)
                .build();
        uploadWindow.setChecklistsDc(checklistsDc);
        uploadWindow.setChecklistsDl(checklistsDl);
        uploadWindow.show();
    }

    @Subscribe("createPopup.copy")
    public void onCreatePopupCopy(Action.ActionPerformedEvent event) {
        if (checklistsTable.getSingleSelected() != null) {
            SimpleChecklist newCl = copyChecklistService.copyChecklist(checklistsTable.getSingleSelected());
            checklistsDc.getMutableItems().add(newCl);

            screenBuilders.editor(checklistsTable)
                    .editEntity(newCl)
                    .build()
                    .show();
        }
    }
}