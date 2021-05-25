package com.company.quarium.service;

import com.company.quarium.entity.checklist.SimpleChecklist;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;

public interface UploadChecklistFromXlsService {
    String NAME = "quarium_UploadChecklistFromXlsService";

    SimpleChecklist createFromXls(File file) throws IOException, InvalidFormatException;
}