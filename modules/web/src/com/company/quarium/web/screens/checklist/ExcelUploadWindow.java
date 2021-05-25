package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.FileMultiUploadField;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private DataManager dataManager;
//    @Inject
//    private CollectionContainer<SimpleChecklist> checklistsDc;

    @Subscribe("multiUploadField")
    public void onMultiUploadFieldQueueUploadComplete(FileMultiUploadField.QueueUploadCompleteEvent event) throws IOException, InvalidFormatException {
        for (Map.Entry<UUID, String> entry : multiUploadField.getUploadsMap().entrySet()) {
            File file = fileUploadingAPI.getFile(entry.getKey());
            UUID fileId = entry.getKey();
            String fileName = entry.getValue();
            FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
            assert file != null;
            XSSFWorkbook newExcel = new XSSFWorkbook(file);
            XSSFSheet newSheet = newExcel.getSheet("test");
            XSSFRow row = newSheet.getRow(0);

            SimpleChecklist checklistNew = dataManager.create(SimpleChecklist.class);
            checklistNew.setName(row.getCell(0).getStringCellValue());
//            checklistsDc.getMutableItems().add(checklistNew);
        }
        notifications.create()
                .withCaption("Uploaded files: " + multiUploadField.getUploadsMap().values())
                .show();
        multiUploadField.clearUploads();
    }
}