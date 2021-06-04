package com.company.quarium.service;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.ProjectVersion;

import java.util.List;

public interface CopyChecklistService {
    String NAME = "quarium_CopyChecklistService";

    SimpleChecklist copyChecklist(Checklist checklist);

    RegressChecklist copyChecklistToRegress(Checklist checklist);

    List<TestCase> copyTestCaseToChecklist(Checklist checklist, TestCase testCase);

    ProjectVersion copyProjectToReleases(Project oldProject);
}