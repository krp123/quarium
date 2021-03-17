package com.company.quarium.web.screens.qa;

import com.company.quarium.entity.references.Qa;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Qa.edit")
@UiDescriptor("qa-edit.xml")
@EditedEntityContainer("qaDc")
@LoadDataBeforeShow
public class QaEdit extends StandardEditor<Qa> {
}