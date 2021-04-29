package com.company.quarium.service;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.project.Project;

public interface CopyChecklistService {
    String NAME = "quarium_CopyChecklistService";

    SimpleChecklist copyChecklist(Checklist checklist, Project project);

    RegressChecklist copyChecklistToRegress(Checklist checklist);
}