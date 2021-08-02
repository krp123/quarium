package com.company.quarium.web.screens.step;

import com.company.quarium.entity.testSuit.Step;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Step.edit")
@UiDescriptor("step-edit.xml")
@EditedEntityContainer("stepDc")
@LoadDataBeforeShow
public class StepEdit extends StandardEditor<Step> {
}