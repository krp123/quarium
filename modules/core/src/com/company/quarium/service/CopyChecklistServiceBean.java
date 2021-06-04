package com.company.quarium.service;

import com.company.quarium.entity.checklist.*;
import com.company.quarium.entity.project.*;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.company.quarium.Constants.PRIORITY_HIGH;

@Service(CopyChecklistService.NAME)
public class CopyChecklistServiceBean implements CopyChecklistService {

    @Inject
    private DataManager dataManager;

    @Override
    public SimpleChecklist copyChecklist(Checklist checklist) {
        SimpleChecklist checklistNew = dataManager.create(SimpleChecklist.class);
        checklistNew.setName(checklist.getName());
        checklistNew.setMinutes(checklist.getMinutes());
        checklistNew.setHours(checklist.getHours());
        checklistNew.setState(checklist.getState());
        checklistNew.setIsUsedInRegress(checklist.getIsUsedInRegress());
        checklistNew.setModule(checklist.getModule());
        checklistNew.setAssignedQa(checklist.getAssignedQa());
        checklistNew.setComment(checklist.getComment());
        checklistNew.setTicket(checklist.getTicket());

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
                        newStep.setNumber(step.getNumber());
                        newStep.setTestCase(newTC);
                        newSteps.add(newStep);
                    }
                    newTC.setCaseStep(newSteps);
                }
                newTC.setExpectedResult(tc.getExpectedResult());
                newTC.setNumber(tc.getNumber());
                newTC.setCreationDate(tc.getCreationDate());
                newTC.setState(tc.getState());
                newTC.setMinutes(tc.getMinutes());
                newTC.setHours(tc.getHours());
                newTC.setComment(tc.getComment());
                newTC.setTicket(tc.getTicket());
                newTC.setCheckDate(tc.getCheckDate());
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
        checklistNew.setAssignedQa(checklist.getAssignedQa());
        checklistNew.setComment(checklist.getComment());
        checklistNew.setHours(checklist.getHours());
        checklistNew.setMinutes(checklist.getMinutes());
        checklistNew.setModule(checklist.getModule());
        checklistNew.setTicket(checklist.getTicket());

        if (checklist.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : checklist.getTestCase()) {
                if (tc.getPriority() != null &&
                        tc.getPriority().getId().equals(PRIORITY_HIGH)) {
                    copyTestCaseToRegress(checklistNew, tcList, tc);
                }
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }

    @Override
    public List<TestCase> copyTestCaseToChecklist(Checklist checklist, TestCase testCase) {
        List<TestCase> tcList = checklist.getTestCase();
        copyTestCase(checklist, tcList, testCase);
        return tcList;

    }

    @Override
    public ProjectVersion copyProjectToReleases(Project oldProject) {
        ProjectVersion release = dataManager.create(ProjectVersion.class);
        release.setProjectName(oldProject.getProjectName());
        release.setCurrentRelease(oldProject.getCurrentRelease());
        release.setDescription(oldProject.getDescription());
        release.setVersionOf(oldProject);
        release.setCreationDate(oldProject.getCreationDate());
        release.setThesisVersion(oldProject.getThesisVersion());
        release.setDbms(oldProject.getDbms());
        release.setRegressStartDate(oldProject.getRegressStartDate());
        release.setRegressFinishDate(oldProject.getRegressFinishDate());

        List<Module> newModules = new ArrayList<>();
        for (Module m : oldProject.getModule()) {
            Module newM = copyModule(m);
            newM.setProject(release);
            newModules.add(newM);
        }
        release.setModule(newModules);


        List<QaProjectRelationship> newQaList = new ArrayList<>();
        for (QaProjectRelationship q : oldProject.getQa()) {
            QaProjectRelationship newQa = copyQaProjectRelationShip(q);
            newQa.setProject(release);
            newQaList.add(newQa);
        }
        release.setQa(newQaList);

        List<ConfigurationProjectRelationship> newConfList = new ArrayList<>();
        for (ConfigurationProjectRelationship c : oldProject.getConfiguration()) {
            ConfigurationProjectRelationship newConf = copyConfiguration(c);
            newConf.setProject(release);
            newConfList.add(newConf);
        }
        release.setConfiguration(newConfList);

        List<SimpleChecklist> newChecklistList = new ArrayList<>();
        for (SimpleChecklist c : oldProject.getChecklist()) {
            SimpleChecklist newChecklist = copyChecklist(c);//TODO изменили копирование. Не добавляется проект. Добавить где-то тут
            newChecklist.setProject(release);
            newChecklistList.add(newChecklist);
        }
        release.setChecklist(newChecklistList);

        List<RegressChecklist> newRegressChecklistList = new ArrayList<>();
        for (RegressChecklist rc : oldProject.getRegressChecklist()) {
            RegressChecklist newRc = copyRegressChecklist(rc, release);
            newRegressChecklistList.add(newRc);
        }
        release.setRegressChecklist(newRegressChecklistList);


        return release;
    }

    private ConfigurationProjectRelationship copyConfiguration(ConfigurationProjectRelationship oldConf) {
        ConfigurationProjectRelationship newConf = dataManager.create(ConfigurationProjectRelationship.class);
        newConf.setConfiguration(oldConf.getConfiguration());
        return newConf;
    }

    private Module copyModule(Module oldModule) {
        Module newModule = dataManager.create(Module.class);
        newModule.setName(oldModule.getName());
        return newModule;
    }

    private QaProjectRelationship copyQaProjectRelationShip(QaProjectRelationship oldQa) {
        QaProjectRelationship newQa = dataManager.create(QaProjectRelationship.class);
        newQa.setQa(oldQa.getQa());
        return newQa;
    }

    public RegressChecklist copyRegressChecklist(Checklist checklist, Project project) {
        RegressChecklist checklistNew = dataManager.create(RegressChecklist.class);
        checklistNew.setName(checklist.getName());
        checklistNew.setProject(project);
        checklistNew.setRegressProject(project);
        checklistNew.setState(checklist.getState());
        checklistNew.setAssignedQa(checklist.getAssignedQa());
        checklistNew.setModule(checklist.getModule());
        checklistNew.setComment(checklist.getComment());
        checklistNew.setMinutes(checklist.getMinutes());
        checklistNew.setHours(checklist.getHours());
        checklistNew.setTicket(checklist.getTicket());
        checklistNew.setParentCard(checklist);
        //TODO проставляется не тот parentCard.
        // Придумать, как можно установить правильный.
        // После этого изменить запрос багов в project-version-edit

        if (checklist.getTestCase() != null) {
            List<TestCase> tcList = new ArrayList<>();
            for (TestCase tc : checklist.getTestCase()) {
                copyTestCase(checklistNew, tcList, tc);
            }
            checklistNew.setTestCase(tcList);
        }
        return checklistNew;
    }

    private void copyTestCase(Checklist checklistNew, List<TestCase> tcList, TestCase tc) {
        TestCase newTC = dataManager.create(TestCase.class);
        newTC.setChecklist(checklistNew);
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
        newTC.setHours(tc.getHours());
        newTC.setMinutes(tc.getMinutes());
        newTC.setTicket(tc.getTicket());
        newTC.setExpectedResult(tc.getName());
        newTC.setPriority(tc.getPriority());
        newTC.setNumber(tc.getNumber());
        newTC.setCreationDate(tc.getCreationDate());
        newTC.setState(tc.getState());
        newTC.setComment(tc.getComment());
        newTC.setCheckDate(tc.getCheckDate());
        tcList.add(newTC);
    }

    private void copyTestCaseToRegress(Checklist checklistNew, List<TestCase> tcList, TestCase tc) {
        TestCase newTC = dataManager.create(TestCase.class);
        newTC.setChecklist(checklistNew);
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
        newTC.setHours(tc.getHours());
        newTC.setMinutes(tc.getMinutes());
        newTC.setExpectedResult(tc.getExpectedResult());
        newTC.setPriority(tc.getPriority());
        newTC.setCreationDate(tc.getCreationDate());
        newTC.setNumber(tc.getNumber());
        tcList.add(newTC);
    }
}