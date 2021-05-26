package com.company.quarium.service;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.checklist.Step;
import com.company.quarium.entity.checklist.TestCase;
import com.haulmont.cuba.core.global.DataManager;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service(UploadChecklistFromXlsService.NAME)
public class UploadChecklistFromXlsServiceBean implements UploadChecklistFromXlsService {
    @Inject
    private DataManager dataManager;

    @Override
    public SimpleChecklist createFromXls(File file) throws IOException, InvalidFormatException {
        XSSFWorkbook newExcel = new XSSFWorkbook(file);
        XSSFSheet newSheet = newExcel.getSheet("Checklist");

        SimpleChecklist checklistNew = dataManager.create(SimpleChecklist.class);

        //Заполняем имя чек-листа из первой строки
        XSSFRow rowName = newSheet.getRow(0);
        checklistNew.setName(rowName.getCell(1).getStringCellValue());

        //Заполняем начальные условия
        XSSFRow rowInitialConditions = newSheet.getRow(1);
        checklistNew.setInitialConditions(rowInitialConditions.getCell(1).getStringCellValue());

        //Заполняем комментарий
        XSSFRow rowComment = newSheet.getRow(2);
        checklistNew.setComment(rowComment.getCell(1).getStringCellValue());

        List<TestCase> testCases = new ArrayList<>();
        int rowsQty = newSheet.getLastRowNum();
        int casesQty = 0;
        for (int i = 4; i < rowsQty; i++) {
            if (newSheet.getRow(i) != null) {
                XSSFRow rowCase = newSheet.getRow(i);
                if (!"".equals(rowCase.getCell(0).getStringCellValue())) {

                    TestCase testCaseNew = dataManager.create(TestCase.class);
                    testCaseNew.setChecklist(checklistNew);
                    testCaseNew.setNumber(++casesQty);

                    //Заполняем наименование кейса
                    testCaseNew.setName(rowCase.getCell(0).getStringCellValue());
                    //Заполняем начальные условия
                    testCaseNew.setInitialConditions(rowCase.getCell(1).getStringCellValue());
                    //Заполняем ожидаемый результат
                    testCaseNew.setExpectedResult(rowCase.getCell(3).getStringCellValue());

                    //Забираем шаги, парсим их по знаку ';' и присваиваем кейсу
                    List<Step> stepsList = new ArrayList<>();
                    String[] steps = rowCase.getCell(2).getStringCellValue().split(";");
                    int stepsQty = 0;
                    for (String s : steps) {
                        Step step = dataManager.create(Step.class);
                        step.setTestCase(testCaseNew);
                        step.setNumber(++stepsQty);
                        step.setStep(s);
                        stepsList.add(step);
                    }
                    testCaseNew.setCaseStep(stepsList);
                    testCases.add(testCaseNew);
                }
            }
        }
        checklistNew.setTestCase(testCases);

        return checklistNew;
    }
}