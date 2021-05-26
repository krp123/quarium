package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.service.UploadChecklistFromXlsService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.FileMultiUploadField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
    protected Metadata metadata;
    @Inject
    private TimeSource timeSource;

    @Subscribe("multiUploadField")
    public void onMultiUploadFieldQueueUploadComplete(FileMultiUploadField.QueueUploadCompleteEvent event) throws IOException, InvalidFormatException {
        for (Map.Entry<UUID, String> entry : multiUploadField.getUploadsMap().entrySet()) {
            File file = fileUploadingAPI.getFile(entry.getKey());
            SimpleChecklist checklistNew = uploadChecklistFromXlsService.createFromXls(file);

            checklistsDc.getMutableItems().add(checklistNew);
            dataManager.commit(checklistNew);
            checklistsDl.load();
        }
        notifications.create()
                .withCaption("Uploaded files: " + multiUploadField.getUploadsMap().values())
                .show();
        multiUploadField.clearUploads();
    }

    public void setChecklistsDc(CollectionContainer<SimpleChecklist> checklistsDc) {
        this.checklistsDc = checklistsDc;
    }

    public void setChecklistsDl(CollectionLoader<SimpleChecklist> checklistsDl) {
        this.checklistsDl = checklistsDl;
    }

    @Subscribe(id = "checklistsDc", target = Target.DATA_CONTAINER)
    private void onChecklistsDcCollectionChange(
            CollectionContainer.CollectionChangeEvent<SimpleChecklist> event) {

    }

    public void downloadTemplate() throws URISyntaxException, IOException, FileStorageException {
        File file = new File(getClass().getResource("com/company/quarium/web/ChecklistImportTemplate.xlsx").toURI());
        AppConfig.createExportDisplay(getWindow()).show(createFileDescriptor(file));
    }

    private FileDescriptor createFileDescriptor(File file) throws FileStorageException, IOException {

        FileDescriptor fd = metadata.create(FileDescriptor.class);

        byte[] data;
        try (FileInputStream fis = FileUtils.openInputStream(file)) {
            data = new byte[(int) file.length()];
            fis.read(data);
        }

        fd.setName(file.getName());
        fd.setExtension(StringUtils.substringAfterLast(file.getName(), "."));
        fd.setCreateDate(timeSource.currentTimestamp());
        fd.setSize((long) data.length);
        return fd;
    }
}