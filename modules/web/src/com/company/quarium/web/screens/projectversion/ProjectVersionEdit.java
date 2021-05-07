package com.company.quarium.web.screens.projectversion;

import com.company.quarium.entity.project.ProjectVersion;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_ProjectVersion.edit")
@UiDescriptor("project-version-edit.xml")
@EditedEntityContainer("projectVersionDc")
@LoadDataBeforeShow
public class ProjectVersionEdit extends StandardEditor<ProjectVersion> {
}