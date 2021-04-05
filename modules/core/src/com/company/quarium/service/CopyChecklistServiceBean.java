package com.company.quarium.service;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.Project;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(CopyChecklistService.NAME)
public class CopyChecklistServiceBean implements CopyChecklistService {

    @Inject
    private DataManager dataManager;

    @Inject
    private Persistence persistence;

    @Override
    public Checklist copyChecklist(Checklist checklist, Project project) {
        Checklist checklistNew = dataManager.create(Checklist.class);
        CommitContext commitContext = new CommitContext();
        checklistNew.setName(checklist.getName());
        checklistNew.setProject(project);
        dataManager.commit(checklistNew);

        commitContext.addInstanceToCommit(checklistNew);
        if (checklist.getTestCase() != null) {
            for (TestCase tc : checklist.getTestCase()) {
                TestCase newTC = dataManager.create(TestCase.class);
                newTC.setChecklist(checklistNew);
                newTC.setName(tc.getName());
                newTC.setStep(tc.getStep());
                newTC.setExpectedResult(tc.getName());
                commitContext.addInstanceToCommit(newTC);
            }
        }
        dataManager.commit(commitContext);
        return checklistNew;
    }
}