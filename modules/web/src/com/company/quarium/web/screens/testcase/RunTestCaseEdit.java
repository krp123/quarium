package com.company.quarium.web.screens.testcase;

import com.company.quarium.entity.testsuit.CaseResult;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.service.TestCaseTimerService;
import com.company.quarium.web.screens.caseresult.CaseResultEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    @Inject
    private DataManager dataManager;
    @Inject
    private GridLayout statusButtonsGrid;
    @Inject
    private HBoxLayout statusBox;
    @Inject
    private Label<CaseStatus> caseResult;

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

        passedButton.setStyleName("passed");
        failedButton.setStyleName("failed");
        skippedButton.setStyleName("skipped");
        blockedButton.setStyleName("blocked");

        if (getEditedEntity().getStatus() != null) {
            caseResult.setStyleName(getEditedEntity().getStatus().name().toLowerCase(Locale.ROOT) + "-result");
        }

        setCasesCount();

        Table.Column<CaseResult> statusColumn = resultsTable.getColumn("status");
        statusColumn.addClickListener(this::openCaseResultViewer);

        Table.Column<CaseResult> dateAddedColumn = resultsTable.getColumn("defectDateAdded");
        dateAddedColumn.addClickListener(this::openCaseResultViewer);

        resultsTable.setStyleProvider(((entity, property) -> {
            switch (entity.getStatus()) {
                case FAILED:
                    return "failed-result";
                case PASSED:
                    return "passed-result";
                case BLOCKED:
                    return "blocked-result";
                case SKIPPED:
                    return "skipped-result";
            }
            return null;
        }));
    }

    private void openCaseResultViewer(Table.Column.ClickEvent<CaseResult> caseResultClickEvent) {
        screenBuilders.editor(CaseResult.class, this)
                .editEntity(caseResultClickEvent.getItem())
                .build()
                .show();
    }

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        resultsTable.setStyleProvider(((entity, property) -> {
            if (property.equals("status")) {
                switch (entity.getStatus()) {
                    case FAILED:
                        return "failed-result";
                    case PASSED:
                        return "passed-result";
                    case BLOCKED:
                        return "blocked-result";
                    case SKIPPED:
                        return "skipped-result";
                }
            }
            return null;
        }));
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
        startTimer();
    }

    private void startTimer() {
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
        adjustCaseResult(CaseStatus.PASSED);
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
        clickNext();
    }

    private void clickNext() {
        if (testCasesDc.getItems().size() > testCasesDc.getItem(testCaseDc.getItem()).getNumber()) {
            stopTimer();
            resetTimerLabel();
            selectCase(testCasesDc.getItem(testCaseDc.getItem()).getNumber() + 1);
        }
    }

    @Subscribe("prev")
    public void onPrevClick(Button.ClickEvent event) {
        if (testCasesDc.getItem(testCaseDc.getItem()).getNumber() > 1) {
            stopTimer();
            resetTimerLabel();
            selectCase(testCasesDc.getItem(testCaseDc.getItem()).getNumber() - 1);
        }
    }

    protected void selectCase(int caseNumber) {
        testCasesDc.replaceItem(testCaseDc.getItem());
        TestCase prevNextCase = testCasesDc.getItems().stream().filter(testCase ->
                testCase.getNumber().equals(caseNumber))
                .findFirst()
                .get();
        getEditedEntityContainer().setItem(prevNextCase);
        setStatus(prevNextCase.getStatus());

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
    }

    @Subscribe("failedButton")
    public void onFailedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.FAILED);
    }

    @Subscribe("skippedButton")
    public void onSkippedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.SKIPPED);
        setStatus(CaseStatus.SKIPPED);
    }

    @Subscribe("blockedButton")
    public void onBlockedButtonClick(Button.ClickEvent event) {
        adjustCaseResult(CaseStatus.BLOCKED);
    }

    private void adjustCaseResult(CaseStatus caseStatus) {
        int resultDcSizeBefore = resultsDc.getItems().size();

        CaseResult newResult = dataManager.create(CaseResult.class);
        newResult.setTestCase(getEditedEntity());
        newResult.setStatus(caseStatus);
        newResult.setExecutionTime(LocalTime.of(
                Integer.valueOf(hours.getValue()),
                Integer.valueOf(minutes.getValue()),
                Integer.valueOf(seconds.getValue())));
        CaseResultEdit caseResultEdit = screenBuilders.editor(CaseResult.class, this)
                .withScreenClass(CaseResultEdit.class)
                .withLaunchMode(OpenMode.DIALOG)
                .newEntity(newResult)
                .build();
        caseResultEdit.show();

        caseResultEdit.addAfterCloseListener(afterCloseEvent -> {
            reloadResultsTable();
            int resultDcSizeAfter = resultsDc.getItems().size();
            if (resultDcSizeAfter > resultDcSizeBefore) {
                clickNext();
            }
        });
    }

    private void reloadResultsTable() {
        resultsDl.load();
        resultsTable.repaint();
    }

    @Subscribe(id = "resultsDc", target = Target.DATA_CONTAINER)
    public void onResultsDcCollectionChange(CollectionContainer.CollectionChangeEvent<CaseResult> event) {
        List<CaseResult> resultsList = resultsDc.getMutableItems();
        resultsList.sort(new Comparator<CaseResult>() {
            @Override
            public int compare(CaseResult o1, CaseResult o2) {
                return o2.getCreateTs().compareTo(o1.getCreateTs());
            }
        });
        if (!resultsList.isEmpty()) {
            CaseStatus lastStatus = resultsList.get(0).getStatus();
            setStatus(lastStatus);
        }
    }

//    @Install(to = "resultsTable", subject = "styleProvider")
//    private String resultsTableStyleProvider(CaseResult entity, String property) {
//        if (property.equals("status")) {
//            switch (entity.getStatus()) {
//                case FAILED:
//                    return "failed-result";
//                case PASSED:
//                    return "passed-result";
//                case BLOCKED:
//                    return "blocked-result";
//                case SKIPPED:
//                    return "skipped-result";
//            }
//        }
//        return null;
//    }

    private void setStatus(CaseStatus caseStatus) {
        if (caseStatus != null) {
            getEditedEntity().setStatus(caseStatus);
            statusButtonsGrid.setVisible(false);
            statusBox.setVisible(true);
            stopTimer();
            startTimer.setEnabled(false);
            caseResult.setStyleName(caseStatus.name().toLowerCase(Locale.ROOT) + "-result");
        } else {
            statusButtonsGrid.setVisible(true);
            statusBox.setVisible(false);
            startTimer.setEnabled(true);
        }
    }

    @Subscribe("retest")
    public void onRetestClick(Button.ClickEvent event) {
        statusButtonsGrid.setVisible(true);
        statusBox.setVisible(false);
        startTimer();
    }
}