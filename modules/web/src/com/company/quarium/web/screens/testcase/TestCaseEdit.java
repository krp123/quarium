package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testSuit.Step;
import com.company.quarium.entity.testSuit.TestCase;
import com.company.quarium.entity.testSuit.TestSuit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.RemoveOperation;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@UiController("quarium_TestCase.edit")
@UiDescriptor("test-case-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class TestCaseEdit extends StandardEditor<TestCase> {
    @Inject
    private DataManager dataManager;
    @Inject
    public InstanceContainer<TestCase> testCaseDc;
    @Inject
    private CollectionPropertyContainer<Step> stepsCollection;
    @Inject
    private Table<Step> stepsTable;
    @Inject
    private TextField<Integer> caseHours;
    @Inject
    private TextField<Integer> caseMinutes;
    @Inject
    private Messages messages;
    public CollectionContainer<TestCase> testCasesDc;
    @Inject
    private Notifications notifications;
    @Inject
    private TextField<String> caseNameField;

    @Subscribe
    public void onInit(InitEvent event) {
        caseHours.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.hoursValidation")));
        caseMinutes.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.minutesValidation")));
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<TestCase> event) {
        adjustStep(event.getEntity());
    }

    public void setCaseNumber(int number) {
        if (getEditedEntity().getNumber() == null)
            getEditedEntity().setNumber(number);
    }

    @Subscribe("stepsTable.createStep")
    public void onCreateStep(Action.ActionPerformedEvent event) {
        Step entity = dataManager.create(Step.class);
        entity.setTestCase(testCaseDc.getItem());
        int lastNum = 0;

        if (testCaseDc
                .getItem()
                .getCaseStep() == null) {
            entity.setNumber(1);
        } else {
            for (Step s : testCaseDc.getItem().getCaseStep()) {
                if (s.getNumber() > lastNum) {
                    lastNum = s.getNumber();
                }
            }
            entity.setNumber(lastNum + 1);
        }
        stepsCollection.getMutableItems().add(entity);
        stepsTable.requestFocus(entity, "step");
    }

    @Install(to = "stepsTable.removeStep", subject = "afterActionPerformedHandler")
    private void stepsTableRemoveStepAfterActionPerformedHandler(RemoveOperation.AfterActionPerformedEvent<Step> afterActionPerformedEvent) {
        List<Step> steps = stepsCollection.getMutableItems();
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setNumber(i + 1);
        }
    }

    @Subscribe("stepsTable.moveStepUp")
    public void onStepsTableMoveStepUp(Action.ActionPerformedEvent event) {
        Step selectedStep = stepsTable.getSingleSelected();
        if (selectedStep != null &&
                selectedStep.getNumber() > 1) {
            stepsCollection.getMutableItems().sort(Comparator.comparing(Step::getNumber));
            Step prevStep = stepsCollection.getMutableItems().get(selectedStep.getNumber() - 2);
            selectedStep.setNumber(selectedStep.getNumber() - 1);
            prevStep.setNumber(prevStep.getNumber() + 1);
            stepsCollection.replaceItem(selectedStep);
            stepsCollection.replaceItem(prevStep);
            stepsCollection.getMutableItems().sort(Comparator.comparing(Step::getNumber));
            stepsTable.repaint();
        }
    }

    @Subscribe("stepsTable.moveStepDown")
    public void onStepsTableMoveStepDown(Action.ActionPerformedEvent event) {
        Step selectedStep = stepsTable.getSingleSelected();
        if (selectedStep != null &&
                selectedStep.getNumber() < stepsCollection.getMutableItems().size()) {
            stepsCollection.getMutableItems().sort(Comparator.comparing(Step::getNumber));
            Step nextStep = stepsCollection.getMutableItems().get(selectedStep.getNumber());
            selectedStep.setNumber(selectedStep.getNumber() + 1);
            nextStep.setNumber(nextStep.getNumber() - 1);
            stepsCollection.replaceItem(selectedStep);
            stepsCollection.replaceItem(nextStep);
            stepsCollection.getMutableItems().sort(Comparator.comparing(Step::getNumber));
            stepsTable.repaint();
        }
    }

    @Subscribe("addNext")
    public void onAddNextClick(Button.ClickEvent event) {
        if (getEditedEntity().getName() == null) {
            caseNameField.focus();
            notifications.create(Notifications.NotificationType.TRAY)
                    .withDescription(String.format(messages.getMessage(getClass(), "fillName")))
                    .show();
        } else {
            if (testCasesDc.containsItem(getEditedEntity())) {
                testCasesDc.replaceItem(getEditedEntity());
            } else {
                testCasesDc.getMutableItems().add(getEditedEntity());
            }

            TestSuit testSuit = getEditedEntity().getTestSuit();
            int number = testCasesDc.getItems().size() + 1;
            TestCase newTestCase = dataManager.create(TestCase.class);
            getEditedEntityContainer().setItem(newTestCase);
            newTestCase.setNumber(number);
            newTestCase.setTestSuit(testSuit);

            adjustStep(newTestCase);
        }
    }

    private void adjustStep(TestCase newTestCase) {
        List<Step> steps = new ArrayList<>();
        Step step = dataManager.create(Step.class);
        step.setNumber(1);
        step.setTestCase(newTestCase);
        steps.add(step);
        newTestCase.setCaseStep(steps);
    }

    public void setTestCasesDc(CollectionContainer<TestCase> testCasesDc) {
        this.testCasesDc = testCasesDc;
    }

    @Subscribe("commitAndCloseBtn")
    public void onCommitAndCloseBtnClick(Button.ClickEvent event) {
        if (getEditedEntity().getName() == null) {
            caseNameField.focus();
            notifications.create(Notifications.NotificationType.TRAY)
                    .withDescription(String.format(messages.getMessage(getClass(), "fillName")))
                    .show();
        } else {
            testCasesDc.replaceItem(testCaseDc.getItem());
            getWindow().getContext().getFrame().getWindowManager().remove(this);
        }
    }
}