package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testsuit.CaseResult;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.service.TestCaseTimerService;
import com.company.quarium.web.screens.caseresult.CaseResultEdit;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@UiController("quarium_RunTestCase.edit")
@UiDescriptor("run-test-case-edit.xml")
@EditedEntityContainer("testCaseDc")
@LoadDataBeforeShow
public class RunTestCaseEdit extends TestCaseEdit {

    @Inject
    private InstanceContainer<TestCase> testCaseDc;
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
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private Table<CaseResult> resultsTable;
    @Inject
    private CollectionLoader<CaseResult> resultsDl;
    @Inject
    private CollectionContainer<CaseResult> resultsDc;

    @Subscribe("timer")
    public void onTimerTimerAction(Timer.TimerActionEvent event) {
        secondsCount++;
        Map<String, String> timeUnits = testCaseTimerService.getTimeUnitsValues(secondsCount);

        hours.setValue(timeUnits.get("hours"));
        minutes.setValue(timeUnits.get("minutes"));
        seconds.setValue(timeUnits.get("seconds"));
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        resultsDl.setParameter("testCase", getEditedEntity());
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

        if (getEditedEntity().getStatus() != null) {
            switch (getEditedEntity().getStatus()) {
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
        setPressedButton(prevNextCase.getStatus());
        setCasesCount();
    }

    @Subscribe(id = "testCaseDc", target = Target.DATA_CONTAINER)
    public void onTestCaseDcItemChange(InstanceContainer.ItemChangeEvent<TestCase> event) {
        resultsDl.setParameter("testCase", event.getItem());
        reloadResultsTable();
    }

    @Subscribe("passedButton")
    public void onPassedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.PASSED);
//        setPressedButton(CaseStatus.PASSED);
    }

    @Subscribe("failedButton")
    public void onFailedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.FAILED);
//        setPressedButton(CaseStatus.FAILED);
    }

    @Subscribe("skippedButton")
    public void onSkippedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.SKIPPED);
        setPressedButton(CaseStatus.SKIPPED);
    }

    @Subscribe("blockedButton")
    public void onBlockedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.BLOCKED);
    }

    private void adjustCaseResult(CaseStatus caseStatus) {
        CaseResultEdit caseResultEdit = screenBuilders.editor(CaseResult.class, this)
                .withScreenClass(CaseResultEdit.class)
                .withLaunchMode(OpenMode.DIALOG)
                .build();
        caseResultEdit.setTestCase(getEditedEntity());
        caseResultEdit.setCaseStatus(caseStatus);
        caseResultEdit.show();

        caseResultEdit.addAfterCloseListener(afterCloseEvent -> {
            reloadResultsTable();
        });
    }

    private void reloadResultsTable() {
        resultsDl.load();
        resultsTable.repaint();
    }

    @Subscribe(id = "resultsDc", target = Target.DATA_CONTAINER)
    public void onResultsDcCollectionChange(CollectionContainer.CollectionChangeEvent<CaseResult> event) {
        List<CaseResult> resultsList = resultsDc.getItems();
        resultsList.stream().sorted(new Comparator<CaseResult>() {
            @Override
            public int compare(CaseResult o1, CaseResult o2) {
                return o1.getCreateTs().compareTo(o2.getCreateTs());
            }
        });
        CaseStatus lastStatus = resultsList.get(0).getStatus();
        setPressedButton(lastStatus);
    }

    private void setPressedButton(CaseStatus caseStatus) {
        getEditedEntity().setStatus(caseStatus);

        passedButton.setStyleName("passed");
        failedButton.setStyleName("failed");
        skippedButton.setStyleName("skipped");
        blockedButton.setStyleName("blocked");

        if (caseStatus != null) {
            switch (caseStatus) {
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