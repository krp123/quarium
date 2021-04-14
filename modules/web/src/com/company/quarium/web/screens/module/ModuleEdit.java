package com.company.quarium.web.screens.module;

import com.company.quarium.entity.project.Module;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Module.edit")
@UiDescriptor("module-edit.xml")
@EditedEntityContainer("moduleDc")
@LoadDataBeforeShow
public class ModuleEdit extends StandardEditor<Module> {
}