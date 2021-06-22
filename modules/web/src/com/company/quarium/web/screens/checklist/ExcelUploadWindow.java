package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.service.UploadChecklistFromXlsService;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.FileMultiUploadField;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.shared.ui.ContentMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@UiController("quarium_Exceluploadwindow")
@UiDescriptor("excelUploadWindow.xml")
public class ExcelUploadWindow extends Screen {
    @Inject
    private FileMultiUploadField multiUploadField;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private Notifications notifications;
    @Inject
    private UploadChecklistFromXlsService uploadChecklistFromXlsService;
    @Inject
    private CollectionContainer<SimpleChecklist> checklistsDc;
    @Inject
    private CollectionLoader<SimpleChecklist> checklistsDl;
    @Inject
    private DataManager dataManager;
    @Inject
    private Label<String> downloadTemplate;
    @Inject
    private Messages messages;


    @Subscribe("multiUploadField")
    public void onMultiUploadFieldQueueUploadComplete(FileMultiUploadField.QueueUploadCompleteEvent event) throws IOException, InvalidFormatException {
        for (Map.Entry<UUID, String> entry : multiUploadField.getUploadsMap().entrySet()) {
            File file = fileUploadingAPI.getFile(entry.getKey());
            SimpleChecklist checklistNew = uploadChecklistFromXlsService.createFromXls(file);

            checklistsDc.getMutableItems().add(checklistNew);
            dataManager.commit(checklistNew);
            checklistsDl.load();
        }
        String files = "";
        for (String str : multiUploadField.getUploadsMap().values()) {
            if (files.equals("")) {
                files = files.concat(str);
            } else {
                files = files.concat(", " + str);
            }
        }
        notifications.create()
                .withCaption(String.format(messages.getMessage(getClass(), "exceluploadwindow.uploadCompleted"))
                        + files)
                .show();
        multiUploadField.clearUploads();
    }

    @Subscribe
    public void onInit(InitEvent event) {
        com.vaadin.ui.Label label = (com.vaadin.ui.Label) WebComponentsHelper.unwrap(downloadTemplate);
        label.setContentMode(ContentMode.HTML);
        String fileName = "ChecklistImportTemplate.xlsx";
        String templateFullPath = AppContext.getProperty("cuba.appDir") + File.separator + "static" + File.separator + fileName;
        File templateFull = new File(templateFullPath);
        if (!templateFull.exists()) {
            label.setVisible(false);
        } else {
            label.setValue(String.format(messages.getMessage(getClass(), "downloadTemplate"),
                    ControllerUtils.getControllerURL("/static/")
                            .replace(StringUtils.substringBeforeLast(
                                    AppBeans.get(Configuration.class).getConfig(GlobalConfig.class).getWebAppUrl(),
                                    "/"), "") + fileName));
        }
    }

    public void setChecklistsDc(CollectionContainer<SimpleChecklist> checklistsDc) {
        this.checklistsDc = checklistsDc;
    }

    public void setChecklistsDl(CollectionLoader<SimpleChecklist> checklistsDl) {
        this.checklistsDl = checklistsDl;
    }
}