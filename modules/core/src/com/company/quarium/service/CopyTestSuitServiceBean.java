package com.company.quarium.service;

import com.company.quarium.entity.testSuit.*;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service(CopyTestSuitService.NAME)
public class CopyTestSuitServiceBean implements CopyTestSuitService {

    @Inject
    private DataManager dataManager;

    @Override
    public SharedTestSuit copyTestSuit(TestSuit testSuit) {
        SharedTestSuit checklistNew = dataManager.create(SharedTestSuit.class);
        checklistNew.setName(testSuit.getName());
        checklistNew.setMinutes(testSuit.getMinutes());
        checklistNew.setHours(testSuit.getHours());
        checklistNew.setState(testSuit.getState());
        checklistNew.setIsUsedInRegress(testSuit.getIsUsedInRegress());
        checklistNew.setModule(testSuit.getModule());
        checklistNew.setAssignedQa(testSuit.getAssignedQa());
        checklistNew.setComment(testSuit.getComment());
        checklistNew.setTicket(testSuit.getTicket());
        checklistNew.setInitialConditions(testSuit.getInitialConditions());

        if (testSuit.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : testSuit.getTestCase()) {
                TestCase newTC = dataManager.create(TestCase.class);
                newTC.setTestSuit(checklistNew);
                newTC.setName(tc.getName());
                if (tc.getCaseStep() != null) {
                    List<Step> newSteps = new ArrayList<>();
                    for (Step step : tc.getCaseStep()) {
                        Step newStep = dataManager.create(Step.class);
                        newStep.setStep(step.getStep());
                        newStep.setNumber(step.getNumber());
                        newStep.setTestCase(newTC);
                        newSteps.add(newStep);
                    }
                    newTC.setCaseStep(newSteps);
                }
                newTC.setExpectedResult(tc.getExpectedResult());
                newTC.setNumber(tc.getNumber());
                newTC.setCreationDate(tc.getCreationDate());
                newTC.setMinutes(tc.getMinutes());
                newTC.setHours(tc.getHours());
                newTC.setComment(tc.getComment());
                newTC.setTicket(tc.getTicket());
                newTC.setCheckDate(tc.getCheckDate());
                newTC.setInitialConditions(tc.getInitialConditions());
                newTC.setPriority(tc.getPriority());
                tcList.add(newTC);
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }

    @Override
    public RunTestSuit copyRunTestSuit(TestSuit testSuit) {
        RunTestSuit checklistNew = dataManager.create(RunTestSuit.class);
        checklistNew.setName(testSuit.getName());
        checklistNew.setMinutes(testSuit.getMinutes());
        checklistNew.setHours(testSuit.getHours());
        checklistNew.setState(testSuit.getState());
        checklistNew.setIsUsedInRegress(testSuit.getIsUsedInRegress());
        checklistNew.setModule(testSuit.getModule());
        checklistNew.setAssignedQa(testSuit.getAssignedQa());
        checklistNew.setComment(testSuit.getComment());
        checklistNew.setTicket(testSuit.getTicket());
        checklistNew.setInitialConditions(testSuit.getInitialConditions());

        if (testSuit.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : testSuit.getTestCase()) {
                TestCase newTC = dataManager.create(TestCase.class);
                newTC.setTestSuit(checklistNew);
                newTC.setName(tc.getName());
                if (tc.getCaseStep() != null) {
                    List<Step> newSteps = new ArrayList<>();
                    for (Step step : tc.getCaseStep()) {
                        Step newStep = dataManager.create(Step.class);
                        newStep.setStep(step.getStep());
                        newStep.setNumber(step.getNumber());
                        newStep.setTestCase(newTC);
                        newSteps.add(newStep);
                    }
                    newTC.setCaseStep(newSteps);
                }
                newTC.setExpectedResult(tc.getExpectedResult());
                newTC.setNumber(tc.getNumber());
                newTC.setCreationDate(tc.getCreationDate());
                newTC.setMinutes(tc.getMinutes());
                newTC.setHours(tc.getHours());
                newTC.setComment(tc.getComment());
                newTC.setTicket(tc.getTicket());
                newTC.setCheckDate(tc.getCheckDate());
                newTC.setInitialConditions(tc.getInitialConditions());
                newTC.setPriority(tc.getPriority());
                tcList.add(newTC);
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }
}