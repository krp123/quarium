package com.company.quarium.web.screens.runtestsuit;

import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.testSuit.TestCase;
import com.company.quarium.web.screens.testSuit.BaseTestSuitEdit;
import com.company.quarium.web.screens.testcase.RunTestCaseEdit;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.List;

@UiController("quarium_RunTestSuit.edit")
@UiDescriptor("runTestSuit-edit.xml")
@EditedEntityContainer("testSuitDc")
@LoadDataBeforeShow
public class RunTestSuitEdit extends BaseTestSuitEdit {
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;
    @Inject
    private Button testRun;
    @Inject
    private Table<TestCase> table;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Messages messages;
    @Inject
    private ScreenBuilders screenBuilders;

    public void setQaParameter(List<QaProjectRelationship> qaProjectRelationship) {
        assignedQaField.setOptionsList(qaProjectRelationship);
    }

    public void setModuleParameter(List<Module> modules) {
        moduleField.setOptionsList(modules);
    }

    @Subscribe("table.run")
    public void onTableRun(Action.ActionPerformedEvent event) {
        TestCase testCase;
        if (table.getSingleSelected() != null) {
            testCase = table.getSingleSelected();
        } else if (!testCasesDc.getItems().isEmpty()) {
            testCase = testCasesDc.getMutableItems().get(0);
        } else {
            return;
        }

        RunTestCaseEdit runTestCaseEdit = screenBuilders.editor(TestCase.class, this)
                .withScreenClass(RunTestCaseEdit.class)
                .editEntity(testCase)
                .withLaunchMode(OpenMode.DIALOG)
                .build();
        runTestCaseEdit.setTestCasesDc(testCasesDc);
        runTestCaseEdit.show();
    }

    @Subscribe
    public void onInit1(InitEvent event) {
        testRun.setStyleName("passed");

        table.addGeneratedColumn("estimation",
                new Table.ColumnGenerator<TestCase>() {
                    @Override
                    public Component generateCell(TestCase testCase) {
                        Label label = uiComponents.create(Label.NAME);
                        int hours = 0;
                        if (testCase.getHours() != null) {
                            hours = testCase.getHours();
                        }
                        int minutes = 0;
                        if (testCase.getMinutes() != null) {
                            minutes = testCase.getMinutes();
                        }
                        String estimation = hours + messages.getMessage(getClass(), "hour") + " "
                                + minutes + messages.getMessage(getClass(), "minute");
                        label.setValue(estimation);
                        return label;
                    }
                });
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        if (testCasesDc.getItems().size() > 0) {
            table.setSelected(testCasesDc.getItems().get(0));
        }
    }
}