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
    public SimpleChecklist createFromXls(File file) throws IOException, InvalidFormatException, IllegalStateException {
        XSSFWorkbook newExcel = new XSSFWorkbook(file);
        XSSFSheet newSheet = newExcel.getSheet("Checklist");

        SimpleChecklist checklistNew = dataManager.create(SimpleChecklist.class);

        //Заполняем имя чек-листа из первой строки
        XSSFRow rowName = newSheet.getRow(0);
        checklistNew.setName(getStringFromCell(rowName, 1));

        //Заполняем начальные условия
        XSSFRow rowInitialConditions = newSheet.getRow(1);
        checklistNew.setInitialConditions(getStringFromCell(rowInitialConditions, 1));

        //Заполняем комментарий
        XSSFRow rowComment = newSheet.getRow(2);
        checklistNew.setComment(rowComment.getCell(1).getStringCellValue());

        //Заполняем оценку
        XSSFRow rowEstimation = newSheet.getRow(3);
        try {
            String estimationString = String.valueOf(rowEstimation.getCell(1).getNumericCellValue());
            if (estimationString.contains(".")) {
                String[] estimationTime = estimationString.split("\\.");
                checklistNew.setHours(Integer.parseInt(estimationTime[0]));
                double minDouble = 60 * Double.parseDouble("0." + estimationTime[1]);
                checklistNew.setMinutes((int) minDouble);
            } else {
                checklistNew.setHours(Integer.parseInt(estimationString));
                checklistNew.setMinutes(0);
            }
        } catch (IllegalStateException ex) {
            checklistNew.setHours(0);
            checklistNew.setMinutes(0);
        }

        //Заполняем тест-кейсы
        List<TestCase> testCases = new ArrayList<>();
        int rowsQty = newSheet.getLastRowNum();
        int casesQty = 0;
        for (int i = 5; i < rowsQty; i++) {
            if (newSheet.getRow(i) != null) {
                XSSFRow rowCase = newSheet.getRow(i);
                String firstCell;
                firstCell = getStringFromCell(rowCase, 0);

                if (!"".equals(firstCell)) {

                    TestCase testCaseNew = dataManager.create(TestCase.class);
                    testCaseNew.setChecklist(checklistNew);
                    testCaseNew.setNumber(++casesQty);

                    //Заполняем наименование кейса
                    testCaseNew.setName(firstCell);

                    //Заполняем начальные условия
                    if (rowCase.getLastCellNum() > 1) {
                        testCaseNew.setInitialConditions(getStringFromCell(rowCase, 1));
                    }

                    //Заполняем ожидаемый результат
                    if (rowCase.getLastCellNum() > 3) {
                        testCaseNew.setExpectedResult(getStringFromCell(rowCase, 3));
                    }

                    //Забираем шаги, парсим их по знаку ';' и присваиваем кейсу
                    List<Step> stepsList = new ArrayList<>();
                    String[] steps = new String[0];
                    if (rowCase.getLastCellNum() > 2) {
                        steps = getStringFromCell(rowCase, 2).split(";");
                    }
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

    private String getStringFromCell(XSSFRow rowCase, int cellNum) {
        String cellValue;
        try {
            cellValue = rowCase.getCell(cellNum).getStringCellValue();
        } catch (IllegalStateException ex) {
            cellValue = rowCase.getCell(cellNum).getRawValue();
        }
        return cellValue;
    }
}