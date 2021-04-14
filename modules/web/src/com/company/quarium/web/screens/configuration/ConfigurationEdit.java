package com.company.quarium.web.screens.configuration;

import com.company.quarium.entity.references.Configuration;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Configuration.edit")
@UiDescriptor("configuration-edit.xml")
@EditedEntityContainer("configurationDc")
@LoadDataBeforeShow
public class ConfigurationEdit extends StandardEditor<Configuration> {
}