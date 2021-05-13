package com.company.quarium.web.screens.thesisversion;

import com.company.quarium.entity.references.ThesisVersion;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_ThesisVersion.edit")
@UiDescriptor("thesis-version-edit.xml")
@EditedEntityContainer("thesisVersionDc")
@LoadDataBeforeShow
public class ThesisVersionEdit extends StandardEditor<ThesisVersion> {
}