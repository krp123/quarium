package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testSuit.CaseResult;
import com.company.quarium.entity.testSuit.TestCase;
import com.company.quarium.service.TestCaseTimerService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Map;

@UiController("quarium_RunTestCase.edit")
@UiDescriptor("run-test-case-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class RunTestCaseEdit extends TestCaseEdit {

    @Inject
    private Label<CaseResult> caseResult;
    @Inject
    private HBoxLayout ticketBox;
    @Inject
    private TextArea<String> caseComment;
    @Inject
    private DateField<LocalDateTime> checkDate;
    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private TimeSource timeSource;
    @Inject
    private Button passedButton;
    @Inject
    private Button skippedButton;
    @Inject
    private Button blockedButton;
    @Inject
    private Button failedButton;
    @Inject
    private Label<String> count;
    @Inject
    private Messages messages;
    @Inject
    private Label<String> resultCaption;
    @Inject
    private Label<String> hours;
    @Inject
    private Label<String> minutes;
    @Inject
    private Label<String> hm;
    @Inject
    private Label<String> ms;
    @Inject
    private Label<String> seconds;

    private static int secondsCount;

    @Inject
    private Timer timer;
    @Inject
    private Button startTimer;
    @Inject
    private Button pauseTimer;
    @Inject
    private Button stopTimer;
    @Inject
    private Button resumeTimer;
    @Inject
    private TestCaseTimerService testCaseTimerService;

    @Subscribe("timer")
    public void onTimerTimerAction(Timer.TimerActionEvent event) {
        secondsCount++;
        Map<String, String> timeUnits = testCaseTimerService.getTimeUnitsValues(secondsCount);

        hours.setValue(timeUnits.get("hours"));
        minutes.setValue(timeUnits.get("minutes"));
        seconds.setValue(timeUnits.get("seconds"));
    }

    @Subscribe("caseResult")
    public void onCaseResultValueChange(HasValue.ValueChangeEvent<CaseResult> event) {
        if (getEditedEntity().getResult() != null) {
            resultCaption.setVisible(true);

            switch (getEditedEntity().getResult()) {
                case FAILED:
                    caseResult.setStyleName("failed-result");
                    ticketBox.setVisible(true);
                    caseComment.setVisible(true);
                    checkDate.setVisible(false);
                    break;

                case PASSED:
                    caseResult.setStyleName("passed-result");
                    checkDate.setVisible(true);
                    ticketBox.setVisible(false);
                    caseComment.setVisible(false);
                    if (testCaseDc.getItem().getCheckDate() == null) {
                        checkDate.setValue(timeSource.now().toLocalDateTime());
                    }
                    break;

                case BLOCKED:
                    caseResult.setStyleName("blocked-result");
                    ticketBox.setVisible(false);
                    caseComment.setVisible(true);
                    checkDate.setVisible(false);
                    break;

                case SKIPPED:
                    caseResult.setStyleName("skipped-result");
                    ticketBox.setVisible(false);
                    caseComment.setVisible(true);
                    checkDate.setVisible(false);
                    break;
            }
        } else {
            resultCaption.setVisible(false);
            ticketBox.setVisible(false);
            caseComment.setVisible(false);
            checkDate.setVisible(false);
        }
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        //Без этого сущность загружается не из датасорса, а из базы. Изменения не сохраняются. Не понял, почему.
        testCaseDc.setItem(testCasesDc.getItem(getEditedEntity()));

        resultCaption.setVisible(false);

        passedButton.setStyleName("passed");
        failedButton.setStyleName("failed");
        skippedButton.setStyleName("skipped");
        blockedButton.setStyleName("blocked");

        if (getEditedEntity().getResult() != null) {
            switch (getEditedEntity().getResult()) {
                case SKIPPED:
                    skippedButton.setStyleName("pressed");
                    break;
                case BLOCKED:
                    blockedButton.setStyleName("pressed");
                    break;
                case PASSED:
                    passedButton.setStyleName("pressed");
                    break;
                case FAILED:
                    failedButton.setStyleName("pressed");
            }
        }
        setCasesCount();
    }

    @Subscribe
    public void onAfterClose(AfterCloseEvent event) {
        secondsCount = 0;
    }

    @Subscribe
    public void onInit1(InitEvent event) {
        resetTimerLabel();
    }

    private void resetTimerLabel() {
        hours.setValue("00");
        hm.setValue(":");
        minutes.setValue("00");
        ms.setValue(":");
        seconds.setValue("00");
        secondsCount = 0;
    }

    @Subscribe("startTimer")
    public void onStartTimerClick(Button.ClickEvent event) {
        resetTimerLabel();
        timer.start();
        startTimer.setVisible(false);
        pauseTimer.setVisible(true);
        resumeTimer.setVisible(false);
        stopTimer.setVisible(true);
    }

    @Subscribe("stopTimer")
    public void onStopTimerClick(Button.ClickEvent event) {
        stopTimer();
    }

    private void stopTimer() {
        timer.stop();
        startTimer.setVisible(true);
        pauseTimer.setVisible(false);
        resumeTimer.setVisible(false);
        stopTimer.setVisible(false);
    }

    @Subscribe("pauseTimer")
    public void onPauseTimerClick(Button.ClickEvent event) {
        timer.stop();
        startTimer.setVisible(false);
        pauseTimer.setVisible(false);
        resumeTimer.setVisible(true);
        stopTimer.setVisible(true);
    }

    @Subscribe("resumeTimer")
    public void onResumeTimerClick(Button.ClickEvent event) {
        timer.start();
        startTimer.setVisible(false);
        pauseTimer.setVisible(true);
        resumeTimer.setVisible(false);
        stopTimer.setVisible(true);
    }

    private void setCasesCount() {
        count.setValue(testCasesDc.getItem(testCaseDc.getItem()).getNumber() + " " +
                messages.getMessage(getClass(), "from") + " " +
                testCasesDc.getItems().size());
    }

    @Subscribe("next")
    public void onNextClick(Button.ClickEvent event) {
        if (testCasesDc.getMutableItems().size() > testCasesDc.getItem(testCaseDc.getItem()).getNumber()) {
            stopTimer();
            resetTimerLabel();
            selectCase(testCasesDc.getItem(testCaseDc.getItem()).getNumber());
        }
    }

    @Subscribe("prev")
    public void onPrevClick(Button.ClickEvent event) {
        if (testCasesDc.getItem(testCaseDc.getItem()).getNumber() > 1) {
            stopTimer();
            resetTimerLabel();
            selectCase(testCasesDc.getItem(testCaseDc.getItem()).getNumber() - 2);
        }
    }

    protected void selectCase(int caseNumber) {
        testCasesDc.replaceItem(getEditedEntity());
        TestCase prevNextCase = testCasesDc.getMutableItems().get(caseNumber);
        getEditedEntityContainer().setItem(prevNextCase);
        setPressedButton(prevNextCase.getResult());
        setCasesCount();
    }

    @Subscribe("passedButton")
    public void onPassedButtonClick(Button.ClickEvent event) {
        setPressedButton(CaseResult.PASSED);
    }

    @Subscribe("failedButton")
    public void onFailedButtonClick(Button.ClickEvent event) {
        setPressedButton(CaseResult.FAILED);
    }

    @Subscribe("skippedButton")
    public void onSkippedButtonClick(Button.ClickEvent event) {
        setPressedButton(CaseResult.SKIPPED);
    }

    @Subscribe("blockedButton")
    public void onBlockedButtonClick(Button.ClickEvent event) {
        setPressedButton(CaseResult.BLOCKED);
    }

    private void setPressedButton(CaseResult result) {
        getEditedEntity().setResult(result);

        passedButton.setStyleName("passed");
        failedButton.setStyleName("failed");
        skippedButton.setStyleName("skipped");
        blockedButton.setStyleName("blocked");

        if (result != null) {
            switch (result) {
                case SKIPPED:
                    skippedButton.setStyleName("pressed");
                    break;
                case BLOCKED:
                    blockedButton.setStyleName("pressed");
                    break;
                case PASSED:
                    passedButton.setStyleName("pressed");
                    break;
                case FAILED:
                    failedButton.setStyleName("pressed");
                    break;
            }
        }
    }
}