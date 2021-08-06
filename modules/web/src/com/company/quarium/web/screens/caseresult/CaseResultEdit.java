package com.company.quarium.web.screens.caseresult;

import com.company.quarium.entity.testsuit.CaseResult;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_CaseResult.edit")
@UiDescriptor("case-result-edit.xml")
@EditedEntityContainer("caseResultDc")
@LoadDataBeforeShow
public class CaseResultEdit extends StandardEditor<CaseResult> {
    @Inject
    private TimeSource timeSource;
    @Inject
    private EntityStates entityStates;
    @Inject
    private Form form;
    @Inject
    private Button commitAndCloseBtn;

    @Subscribe
    public void onInitEntity(InitEntityEvent<CaseResult> event) {
        event.getEntity().setDateAdded(timeSource.now().toLocalDateTime());
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (!entityStates.isNew(getEditedEntity())) {
            form.setEditable(false);
            commitAndCloseBtn.setVisible(false);
        }
    }
}