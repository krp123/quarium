package com.company.quarium.web.screens.regresschecklist;

import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.web.screens.checklist.ExtChecklistEdit;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.List;

@UiController("quarium_RegressChecklist.edit")
@UiDescriptor("regress-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class RegressChecklistEdit extends ExtChecklistEdit {
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;

    public void setQaParameter(List<QaProjectRelationship> qaProjectRelationship) {
        assignedQaField.setOptionsList(qaProjectRelationship);
    }

    public void setModuleParameter(List<Module> modules) {
        moduleField.setOptionsList(modules);
    }
}