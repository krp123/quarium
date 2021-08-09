package com.company.quarium.web.screens.caseresult;

import com.company.quarium.entity.testsuit.CaseResult;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
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
    @Inject
    private LookupField<CaseStatus> statusField;

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

    @Install(to = "statusField", subject = "optionStyleProvider")
    private String statusFieldOptionStyleProvider(CaseStatus caseStatus) {
        if (caseStatus != null) {
            switch (caseStatus) {
                case PASSED:
                    return "passed-result";
                case FAILED:
                    return "failed-result";
                case BLOCKED:
                    return "blocked-result";
                case SKIPPED:
                    return "skipped-result";
            }
        }
        return null;
    }

    @Subscribe("statusField")
    public void onStatusFieldValueChange(HasValue.ValueChangeEvent<CaseStatus> event) {
        if (event.getValue() != null) {
            switch (event.getValue()) {
                case PASSED:
                    statusField.setStyleName("passed-result");
                    return;
                case FAILED:
                    statusField.setStyleName("failed-result");
                    return;
                case BLOCKED:
                    statusField.setStyleName("blocked-result");
                    return;
                case SKIPPED:
                    statusField.setStyleName("skipped-result");
            }
        }
    }
}