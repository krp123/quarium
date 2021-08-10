package com.company.quarium.web.screens.testsuit;

import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Statement;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.company.quarium.entity.testsuit.Step;
import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.entity.testsuit.TestSuit;
import com.company.quarium.web.screens.testcase.TestCaseEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.RemoveOperation;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.EntityLogItem;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.company.quarium.Constants.STATE_CHECKED;

@UiController("quarium_BaseTestSuit.edit")
@UiDescriptor("baseTestSuit-edit.xml")
@EditedEntityContainer("testSuitDc")
@LoadDataBeforeShow
public class BaseTestSuitEdit extends StandardEditor<TestSuit> {
    @Inject
    protected CollectionContainer<TestCase> testCasesDc;
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;
    @Inject
    private DataManager dataManager;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Button testCaseUp;
    @Inject
    private Button testCaseDown;
    @Inject
    protected CollectionLoader<EntityLogItem> entityLogItemsDl;
    @Inject
    protected CollectionContainer<EntityLogItem> entitylogsDc;
    @Inject
    protected CollectionLoader<EntityLogItem> testCaseLogItemsDl;
    @Inject
    protected CollectionLoader<EntityLogItem> stepLogItemsDl;
    @Inject
    protected CollectionContainer<EntityLogItem> testCaseLogsDc;
    @Inject
    protected CollectionContainer<EntityLogItem> stepLogsDc;
    @Inject
    protected Table<EntityLogItem> logTable;
    @Inject
    private TextField<Integer> checklistHours;
    @Inject
    private Messages messages;
    @Inject
    private TextField<Integer> checklistMinutes;
    @Inject
    private LookupField<Statement> stateField;
    @Inject
    private Table<TestCase> table;

    @Subscribe
    protected void onInit(InitEvent event) {
        checklistHours.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.hoursValidation")));
        checklistMinutes.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.minutesValidation")));

        entityLogItemsDl.setParameter("testSuit", getEditedEntity());
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            testCaseUp.setEnabled(false);
            testCaseDown.setEnabled(false);
        }
    }

    @Subscribe("getEstimation")
    protected void onGetEstimationPerformed(Action.ActionPerformedEvent event) {
        List<TestCase> cases = getEditedEntity().getTestCase();
        int generalTimeMinutes = 0;
        int checklistHours = 0;
        int checklistMinutes = 0;

        if (cases != null) {
            for (TestCase testCase : cases) {
                if (testCase.getHours() != null) {
                    generalTimeMinutes += testCase.getHours() * 60;
                }

                if (testCase.getMinutes() != null) {
                    generalTimeMinutes += testCase.getMinutes();
                }
            }
        }

        if (generalTimeMinutes > 0) {
            checklistHours = generalTimeMinutes / 60;
            checklistMinutes = generalTimeMinutes % 60;
        }

        getEditedEntity().setHours(checklistHours);
        getEditedEntity().setMinutes(checklistMinutes);
    }

    @Install(to = "table.remove", subject = "afterActionPerformedHandler")
    private void tableRemoveAfterActionPerformedHandler(RemoveOperation.AfterActionPerformedEvent<TestCase> afterActionPerformedEvent) {
        List<TestCase> cases = testCasesDc.getMutableItems();
        for (int i = 0; i < cases.size(); i++) {
            cases.get(i).setNumber(i + 1);
        }
    }

    @Subscribe("table.moveTestCaseUp")
    public void onTableMoveTestCaseUp(Action.ActionPerformedEvent event) {
        TestCase selectedCase = table.getSingleSelected();
        if (selectedCase != null &&
                selectedCase.getNumber() > 1) {
            testCasesDc.getMutableItems().sort(Comparator.comparing(TestCase::getNumber));
            TestCase prevCase = testCasesDc.getMutableItems().get(selectedCase.getNumber() - 2);
            selectedCase.setNumber(selectedCase.getNumber() - 1);
            prevCase.setNumber(prevCase.getNumber() + 1);
            testCasesDc.replaceItem(selectedCase);
            testCasesDc.replaceItem(prevCase);
            testCasesDc.getMutableItems().sort(Comparator.comparing(TestCase::getNumber));
            table.repaint();
        }
    }

    @Subscribe("table.moveTestCaseDown")
    public void onTableMoveTestCaseDown(Action.ActionPerformedEvent event) {
        TestCase selectedCase = table.getSingleSelected();
        if (selectedCase != null &&
                selectedCase.getNumber() < testCasesDc.getMutableItems().size()) {
            testCasesDc.getMutableItems().sort(Comparator.comparing(TestCase::getNumber));
            TestCase nextCase = testCasesDc.getMutableItems().get(selectedCase.getNumber());
            selectedCase.setNumber(selectedCase.getNumber() + 1);
            nextCase.setNumber(nextCase.getNumber() - 1);
            testCasesDc.replaceItem(nextCase);
            testCasesDc.replaceItem(nextCase);
            testCasesDc.getMutableItems().sort(Comparator.comparing(TestCase::getNumber));
            table.repaint();
        }
    }

    public void setQaParameter(List<QaProjectRelationship> qaProjectRelationship) {
        assignedQaField.setOptionsList(qaProjectRelationship);
    }

    @Install(to = "table.create", subject = "screenConfigurer")
    private void tableCreateScreenConfigurer(Screen screen) {
        if (testCasesDc.getItems().size() != 0) {
            ((TestCaseEdit) screen).setCaseNumber(testCasesDc.getItems().size() + 1);
        } else {
            ((TestCaseEdit) screen).setCaseNumber(1);
        }

        ((TestCaseEdit) screen).setTestCasesDc(testCasesDc);
    }

    @Install(to = "table.edit", subject = "screenConfigurer")
    public void tableEditScreenConfigurer(Screen screen) {
        ((TestCaseEdit) screen).setTestCasesDc(testCasesDc);
    }

    public void setModuleParameter(List<Module> modules) {
        moduleField.setOptionsList(modules);
    }

    @Subscribe(id = "testSuitDc", target = Target.DATA_CONTAINER)
    public void onChecklistDcItemChange(InstanceContainer.ItemChangeEvent<TestSuit> event) {
        List<Step> stepsList = new ArrayList<>();
        if (event.getItem().getTestCase() != null) {
            for (TestCase tc : event.getItem().getTestCase()) {
                if (tc.getCaseStep() != null) {
                    stepsList.addAll(tc.getCaseStep());
                }
            }
        }
        entityLogItemsDl.setParameter("testSuit", Objects.requireNonNull(event.getItem()).getId());
        testCaseLogItemsDl.setParameter("testCases", event.getItem().getTestCase());
        stepLogItemsDl.setParameter("steps", stepsList);
    }

    @Install(to = "entityLogItemsDl", target = Target.DATA_LOADER)
    public List<EntityLogItem> entityLogItemsDlLoadDelegate(LoadContext<EntityLogItem> loadContext) {
        List<EntityLogItem> entityLogList = new ArrayList();
        entityLogList.addAll(dataManager.loadList(loadContext));
        testCaseLogItemsDl.load();
        stepLogItemsDl.load();
        entityLogList.addAll(testCaseLogsDc.getItems());
        entityLogList.addAll(stepLogsDc.getItems());
        return entityLogList;
    }

    @Subscribe(id = "testCasesDc", target = Target.DATA_CONTAINER)
    public void onTestCasesDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<TestCase> event) {
        if (!event.getProperty().equals("status")) {
            return;
        }

        Statement completedState = dataManager.load(Statement.class)
                .id(STATE_CHECKED).one();

        boolean hasUnchecked = false;
        for (TestCase tc : testCasesDc.getItems()) {
            if (tc.getStatus() == null ||
                    !tc.getStatus().equals(CaseStatus.PASSED) &&
                            !tc.getStatus().equals(CaseStatus.SKIPPED)) {
                hasUnchecked = true;
            }
        }

        if (!hasUnchecked) {
            stateField.setValue(completedState);
        }
    }

//    @Subscribe("stateField")
//    public void onStateFieldValueChange(HasValue.ValueChangeEvent<Statement> event) {
//        if (event.getValue() != null &&
//                event.getValue().getId().equals(UUID.fromString("d9d8fd34-068d-99db-5adc-9d95731bc419"))) {
//            for (TestCase tc : testCasesDc.getMutableItems()) {
//                if (tc.getResult() == null ||
//                        !tc.getResult().equals(CaseResult.PASSED) &&
//                                !tc.getResult().equals(CaseResult.SKIPPED)) {
//                    tc.setResult(CaseResult.PASSED);
//                    tc.setCheckDate(timeSource.now().toLocalDateTime());
//                }
//            }
//        }
//    }
//
//    @Install(to = "caseResult", subject = "optionStyleProvider")
//    protected String caseResultOptionStyleProvider(CaseResult caseResult) {
//        if (caseResult != null) {
//            switch (caseResult) {
//                case PASSED:
//                    return "passed-result";
//                case FAILED:
//                    return "failed-result";
//                case BLOCKED:
//                    return "blocked-result";
//                case SKIPPED:
//                    return "skipped-result";
//            }
//        }
//        return null;
//    }
//
//    @Subscribe("caseResult")
//    public void onCaseResultValueChange(HasValue.ValueChangeEvent<CaseResult> event) {
//        if (event.getValue() != null) {
//            switch (event.getValue()) {
//                case PASSED:
//                    caseResult.setStyleName("passed-result");
//                    return;
//                case FAILED:
//                    caseResult.setStyleName("failed-result");
//                    return;
//                case BLOCKED:
//                    caseResult.setStyleName("blocked-result");
//                    return;
//                case SKIPPED:
//                    caseResult.setStyleName("skipped-result");
//                    return;
//            }
//        }
//    }
}