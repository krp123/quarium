package com.company.quarium.web.screens.milestone;

import com.company.quarium.entity.project.Milestone;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_Milestone.edit")
@UiDescriptor("milestone-edit.xml")
@EditedEntityContainer("milestoneDc")
@LoadDataBeforeShow
public class MilestoneEdit extends StandardEditor<Milestone> {
    @Inject
    private TimeSource timeSource;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Milestone> event) {
        event.getEntity().setStartDate(timeSource.now().toLocalDate());
    }
}