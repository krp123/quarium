package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testsuit.CaseResult;
import com.company.quarium.entity.testsuit.TestCase;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Link;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.Date;

@UiController("quarium_Defect.edit")
@UiDescriptor("defect-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class DefectEdit extends StandardEditor<TestCase> {
    @Inject
    private CollectionLoader<CaseResult> resultsDl;
    @Inject
    private CollectionContainer<CaseResult> resultsDc;
    @Inject
    private TextArea<String> commentField;
    @Inject
    private Link link;
    @Inject
    private DateField<Date> dateAdded;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        resultsDl.setParameter("testCase", getEditedEntity());
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (!resultsDc.getItems().isEmpty()) {
            CaseResult lastResult = resultsDc.getItems().get(resultsDc.getItems().size() - 1);
            if (lastResult.getComment() != null) {
                commentField.setValue(lastResult.getComment());
            }
            if (lastResult.getLink() != null) {
                link.setCaption(lastResult.getLink());
                link.setRel(lastResult.getLink());
                link.setUrl(lastResult.getLink());
            }
            if (lastResult.getDateAdded() != null) {
                dateAdded.setValue(Date.from(lastResult.getDateAdded().atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
    }
}