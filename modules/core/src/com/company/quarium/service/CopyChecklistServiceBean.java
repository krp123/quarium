package com.company.quarium.service;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.TestCase;
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
    public Checklist copyChecklist(Checklist checklist, Project project) {
        Checklist checklistNew = dataManager.create(Checklist.class);
        checklistNew.setName(checklist.getName());
        checklistNew.setProject(project);

        if (checklist.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : checklist.getTestCase()) {
                TestCase newTC = dataManager.create(TestCase.class);
                newTC.setChecklist(checklistNew);
                newTC.setName(tc.getName());
                newTC.setStep(tc.getStep());
                newTC.setExpectedResult(tc.getName());
                tcList.add(newTC);
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }
}