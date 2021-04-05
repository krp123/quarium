package com.company.quarium.service;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.project.Project;

public interface CopyChecklistService {
    String NAME = "quarium_CopyChecklistService";

    Checklist copyChecklist(Checklist checklist, Project project);
}