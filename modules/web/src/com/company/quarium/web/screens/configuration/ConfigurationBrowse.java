package com.company.quarium.web.screens.configuration;

import com.company.quarium.entity.references.Configuration;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Configuration.browse")
@UiDescriptor("configuration-browse.xml")
@LookupComponent("configurationsTable")
@LoadDataBeforeShow
public class ConfigurationBrowse extends StandardLookup<Configuration> {
}