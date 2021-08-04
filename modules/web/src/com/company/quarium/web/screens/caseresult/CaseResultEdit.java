package com.company.quarium.web.screens.caseresult;

import com.company.quarium.entity.testsuit.CaseResult;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_CaseResult.edit")
@UiDescriptor("case-result-edit.xml")
@EditedEntityContainer("caseResultDc")
@LoadDataBeforeShow
public class CaseResultEdit extends StandardEditor<CaseResult> {
    @Inject
    private TimeSource timeSource;

    @Subscribe
    public void onInitEntity(InitEntityEvent<CaseResult> event) {
        event.getEntity().setDateAdded(timeSource.now().toLocalDateTime());
    }
}