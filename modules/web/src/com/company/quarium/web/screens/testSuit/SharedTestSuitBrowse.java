package com.company.quarium.web.screens.testSuit;

import com.company.quarium.entity.testsuit.SharedTestSuit;
import com.company.quarium.entity.testsuit.TestSuit;
import com.company.quarium.service.CopyTestSuitService;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.PopupButton;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_SharedTestSuit.browse")
@UiDescriptor("sharedTestSuit-browse.xml")
@LookupComponent("testSuitsTable")
@LoadDataBeforeShow
public class SharedTestSuitBrowse extends StandardLookup<TestSuit> {
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionContainer<SharedTestSuit> testSuitsDc;
    @Inject
    private CollectionLoader<SharedTestSuit> testSuitsDl;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private PopupButton createPopup;
    @Inject
    private GroupTable<SharedTestSuit> testSuitsTable;
    @Inject
    private CopyTestSuitService copyTestSuitService;

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
        uploadWindow.setTestSuitsDc(testSuitsDc);
        uploadWindow.setChecklistsDl(testSuitsDl);
        uploadWindow.show();
    }

    @Subscribe("createPopup.copy")
    public void onCreatePopupCopy(Action.ActionPerformedEvent event) {
        if (testSuitsTable.getSingleSelected() != null) {
            SharedTestSuit newCl = copyTestSuitService.copyTestSuit(testSuitsTable.getSingleSelected());
            testSuitsDc.getMutableItems().add(newCl);

            screenBuilders.editor(testSuitsTable)
                    .editEntity(newCl)
                    .build()
                    .show();
        }
    }
}