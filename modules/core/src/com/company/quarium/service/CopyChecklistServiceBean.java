package com.company.quarium.service;

import com.company.quarium.entity.checklist.*;
import com.company.quarium.entity.project.Project;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service(CopyChecklistService.NAME)
public class CopyChecklistServiceBean implements CopyChecklistService {

    @Inject
    private DataManager dataManager;

    @Override
    public SimpleChecklist copyChecklist(Checklist checklist, Project project) {
        SimpleChecklist checklistNew = dataManager.create(SimpleChecklist.class);
        checklistNew.setName(checklist.getName());
        checklistNew.setProject(project);

        if (checklist.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : checklist.getTestCase()) {
                TestCase newTC = dataManager.create(TestCase.class);
                newTC.setChecklist(checklistNew);
                newTC.setName(tc.getName());
                if (tc.getCaseStep() != null) {
                    List<Step> newSteps = new ArrayList<>();
                    for (Step step : tc.getCaseStep()) {
                        Step newStep = dataManager.create(Step.class);
                        newStep.setStep(step.getStep());
                        newStep.setCreationDate(step.getCreationDate());
                        newStep.setTestCase(newTC);
                        newSteps.add(newStep);
                    }
                    newTC.setCaseStep(newSteps);
                }
                newTC.setExpectedResult(tc.getName());
                tcList.add(newTC);
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }

    @Override
    public RegressChecklist copyChecklistToRegress(Checklist checklist) {
        RegressChecklist checklistNew = dataManager.create(RegressChecklist.class);
        checklistNew.setName(checklist.getName());
        checklistNew.setParentCard(checklist);
        checklistNew.setRegressProject(checklist.getProject());

        if (checklist.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : checklist.getTestCase()) {
                if (tc.getPriority() != null &&
                        tc.getPriority().getId().toString().equals("e2e009c7-4f9c-be4a-6b0e-a9d7c9db7dd0")) {
                    TestCase newTC = dataManager.create(TestCase.class);
                    newTC.setChecklist(checklistNew);
                    newTC.setName(tc.getName());
                    if (tc.getCaseStep() != null) {
                        List<Step> newSteps = new ArrayList<>();
                        for (Step step : tc.getCaseStep()) {
                            Step newStep = dataManager.create(Step.class);
                            newStep.setStep(step.getStep());
                            newStep.setCreationDate(step.getCreationDate());
                            newStep.setTestCase(newTC);
                            newSteps.add(newStep);
                        }
                        newTC.setCaseStep(newSteps);
                    }
                    newTC.setExpectedResult(tc.getName());
                    newTC.setPriority(tc.getPriority());
                    newTC.setCreationDate(tc.getCreationDate());
                    tcList.add(newTC);
                }
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }

    @Override
    public List<TestCase> copyTestCaseToChecklist(Checklist checklist, TestCase testCase) {
        List<TestCase> tcList = checklist.getTestCase();
        TestCase newTestCase = dataManager.create(TestCase.class);
        newTestCase.setChecklist(checklist);
        newTestCase.setName(testCase.getName());
        if (testCase.getCaseStep() != null) {
            List<Step> newSteps = new ArrayList<>();
            for (Step step : testCase.getCaseStep()) {
                Step newStep = dataManager.create(Step.class);
                newStep.setStep(step.getStep());
                newStep.setCreationDate(step.getCreationDate());
                newStep.setTestCase(newTestCase);
                newSteps.add(newStep);
            }
            newTestCase.setCaseStep(newSteps);
        }
        newTestCase.setExpectedResult(testCase.getName());
        newTestCase.setPriority(testCase.getPriority());
        newTestCase.setCreationDate(testCase.getCreationDate());
        tcList.add(newTestCase);
        return tcList;

    }//TODO вставить этот метод в copyChecklistToRegress()
}