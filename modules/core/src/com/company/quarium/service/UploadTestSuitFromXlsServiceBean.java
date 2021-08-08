package com.company.quarium.service;

import com.company.quarium.entity.references.Priority;
import com.company.quarium.entity.testsuit.SharedTestSuit;
import com.company.quarium.entity.testsuit.Step;
import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.core.global.DataManager;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.company.quarium.Constants.*;

@Service(UploadTestSuitFromXlsService.NAME)
public class UploadTestSuitFromXlsServiceBean implements UploadTestSuitFromXlsService {
    @Inject
    private DataManager dataManager;

    @Override
    public SharedTestSuit createFromXls(File file) throws IOException, InvalidFormatException, IllegalStateException {
        XSSFWorkbook newExcel = new XSSFWorkbook(file);
        XSSFSheet newSheet = newExcel.getSheet("TestSuit");

        SharedTestSuit testSuitNew = dataManager.create(SharedTestSuit.class);

        //Заполняем имя чек-листа из первой строки
        XSSFRow rowName = newSheet.getRow(0);
        testSuitNew.setName(getStringFromCell(rowName, 1));

        //Заполняем начальные условия
        XSSFRow rowInitialConditions = newSheet.getRow(1);
        testSuitNew.setInitialConditions(getStringFromCell(rowInitialConditions, 1));

        //Заполняем комментарий
        XSSFRow rowComment = newSheet.getRow(2);
        testSuitNew.setComment(getStringFromCell(rowComment, 1));

        //Заполняем оценку
        XSSFRow rowEstimation = newSheet.getRow(3);
        try {
            String estimationString = String.valueOf(rowEstimation.getCell(1).getNumericCellValue());
            if (estimationString.contains(".")) {
                String[] estimationTime = estimationString.split("\\.");
                testSuitNew.setHours(Integer.parseInt(estimationTime[0]));
                double minDouble = 60 * Double.parseDouble("0." + estimationTime[1]);
                testSuitNew.setMinutes((int) minDouble);
            } else {
                testSuitNew.setHours(Integer.parseInt(estimationString));
                testSuitNew.setMinutes(0);
            }
        } catch (IllegalStateException ex) {
            testSuitNew.setHours(0);
            testSuitNew.setMinutes(0);
        }

        //Заполняем ссылку
        XSSFRow rowLink = newSheet.getRow(4);
        testSuitNew.setTicket(getStringFromCell(rowLink, 1));

        //Заполняем тест-кейсы
        List<TestCase> testCases = new ArrayList<>();
        int rowsQty = newSheet.getLastRowNum();
        int casesQty = 0;
        for (int i = 6; i < rowsQty; i++) {
            if (newSheet.getRow(i) != null) {
                XSSFRow rowCase = newSheet.getRow(i);
                String firstCell;
                firstCell = getStringFromCell(rowCase, 0);

                if (!"".equals(firstCell)) {

                    TestCase testCaseNew = dataManager.create(TestCase.class);
                    testCaseNew.setTestSuit(testSuitNew);
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

                    //Заполняем приоритет
                    if (rowCase.getLastCellNum() > 4) {
                        try {
                            Map<Integer, UUID> priorityMap = new HashMap<>();
                            priorityMap.put(1, PRIORITY_LOW);
                            priorityMap.put(2, PRIORITY_MEDIUM);
                            priorityMap.put(3, PRIORITY_HIGH);
                            Priority rowPriority = dataManager.getReference(Priority.class,
                                    priorityMap.get(((int) rowCase.getCell(4).getNumericCellValue())));
                            testCaseNew.setPriority(rowPriority);
                        } catch (IllegalStateException ignored) {

                        }
                    }
                }
            }
        }
        testSuitNew.setTestCase(testCases);

        return testSuitNew;
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