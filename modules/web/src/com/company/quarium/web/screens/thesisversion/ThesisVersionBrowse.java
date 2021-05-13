package com.company.quarium.web.screens.thesisversion;

import com.company.quarium.entity.references.ThesisVersion;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_ThesisVersion.browse")
@UiDescriptor("thesis-version-browse.xml")
@LookupComponent("thesisVersionsTable")
@LoadDataBeforeShow
public class ThesisVersionBrowse extends StandardLookup<ThesisVersion> {
}