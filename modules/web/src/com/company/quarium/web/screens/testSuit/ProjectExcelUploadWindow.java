package com.company.quarium.web.screens.testSuit;

import com.company.quarium.entity.project.SimpleProject;
import com.company.quarium.entity.testsuit.SharedTestSuit;
import com.company.quarium.service.UploadTestSuitFromXlsService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.FileMultiUploadField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@UiController("quarium_ProjectExceluploadwindow")
@UiDescriptor("projectExcelUploadWindow.xml")
public class ProjectExcelUploadWindow extends ExcelUploadWindow {
    @Inject
    private FileMultiUploadField multiUploadField;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private Notifications notifications;
    @Inject
    private UploadTestSuitFromXlsService uploadTestSuitFromXlsService;
    @Inject
    private CollectionContainer<SharedTestSuit> testSuitsDc;
    @Inject
    private InstanceContainer<SimpleProject> projectDc;
    @Inject
    private Messages messages;

    @Override
    public void onMultiUploadFieldQueueUploadComplete(FileMultiUploadField.QueueUploadCompleteEvent event) throws IOException, InvalidFormatException {
        for (Map.Entry<UUID, String> entry : multiUploadField.getUploadsMap().entrySet()) {
            File file = fileUploadingAPI.getFile(entry.getKey());
            SharedTestSuit testSuitNew = uploadTestSuitFromXlsService.createFromXls(file);
            testSuitNew.setProject(projectDc.getItem());
            testSuitsDc.getMutableItems().add(testSuitNew);
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

    public void setTestSuitsDc(CollectionContainer<SharedTestSuit> testSuitsDc) {
        this.testSuitsDc = testSuitsDc;
    }

    public void setProject(SimpleProject project) {
        projectDc.setItem(project);
    }
}