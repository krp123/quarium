package com.company.quarium.web.screens.caseresult;

import com.company.quarium.entity.testsuit.CaseResult;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_CaseResult.edit")
@UiDescriptor("case-result-edit.xml")
@EditedEntityContainer("caseResultDc")
@LoadDataBeforeShow
public class CaseResultEdit extends StandardEditor<CaseResult> {
    private TestCase testCase;
    private CaseStatus caseStatus;
    @Inject
    private CollectionContainer<CaseResult> resultsDc;
    @Inject
    private CollectionLoader<CaseResult> resultsDl;
    @Inject
    private TimeSource timeSource;

    @Subscribe
    public void onInitEntity(InitEntityEvent<CaseResult> event) {
        event.getEntity().setTestCase(testCase);
        event.getEntity().setStatus(caseStatus);
        event.getEntity().setDateAdded(timeSource.now().toLocalDateTime());
    }

    @Subscribe
    public void onInit(InitEvent event) {
        resultsDl.setParameter("testCase", testCase);
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public void setCaseStatus(CaseStatus caseStatus) {
        this.caseStatus = caseStatus;
    }
}