package com.company.quarium.web.screens.project;

import com.company.quarium.entity.Project;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Project.browse")
@UiDescriptor("project-browse.xml")
@LookupComponent("projectsTable")
@LoadDataBeforeShow
public class ProjectBrowse extends StandardLookup<Project> {
}