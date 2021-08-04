package com.company.quarium.service;

import com.company.quarium.entity.testsuit.SharedTestSuit;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;

public interface UploadTestSuitFromXlsService {
    String NAME = "quarium_UploadTestSuitFromXlsService";

    SharedTestSuit createFromXls(File file) throws IOException, InvalidFormatException;
}