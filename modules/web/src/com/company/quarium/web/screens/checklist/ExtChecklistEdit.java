package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.CaseResult;
import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.Step;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Priority;
import com.company.quarium.entity.references.Statement;
import com.haulmont.cuba.core.app.LockService;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.RemoveOperation;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.actions.list.EditAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.DataUnit;
import com.haulmont.cuba.gui.components.data.Options;
import com.haulmont.cuba.gui.components.data.meta.ContainerDataUnit;
import com.haulmont.cuba.gui.components.data.options.ContainerOptions;
import com.haulmont.cuba.gui.dynamicattributes.DynamicAttributesGuiTools;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.util.OperationResult;
import com.haulmont.cuba.security.entity.EntityLogItem;
import com.haulmont.cuba.security.entity.EntityOp;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

@UiController("ext_quarium_Checklist.edit")
@UiDescriptor("ext-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class ExtChecklistEdit extends StandardEditor<Checklist> {

    protected boolean editing;
    protected boolean creating;
    protected boolean justLocked;

    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private CollectionContainer<TestCase> testCasesDc;
    @Inject
    private CollectionContainer<Step> stepsCollection;
    @Inject
    private Table<TestCase> table;
    @Inject
    private Table<Step> stepsTable;
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;
    @Inject
    private DataManager dataManager;
    @Inject
    private TimeSource timeSource;
    @Inject
    private TextArea<String> caseComment;
    @Inject
    private HBoxLayout ticketBox;
    @Inject
    private ScreenValidation screenValidation;
    @Inject
    private DateField<LocalDateTime> checkDate;
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
    private TextField<Integer> caseHours;
    @Inject
    private TextField<Integer> caseMinutes;
    @Inject
    private LookupField<CaseResult> caseResult;

    private TestCase tempCase;
    @Inject
    private LookupField<Statement> stateField;
    @Inject
    private TextField<String> caseNameField;
    @Inject
    private TextArea<String> initialConditions;
    @Inject
    private LookupField<Priority> priorityField;
    @Inject
    private TextArea<String> expectedResultField;

    @Subscribe
    protected void onInit(InitEvent event) {
        initMasterDetailScreen(event);
        checklistHours.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.hoursValidation")));
        checklistMinutes.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.minutesValidation")));
        caseHours.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.hoursValidation")));
        caseMinutes.setConversionErrorMessage(
                String.format(messages.getMessage(getClass(), "checklistEdit.minutesValidation")));
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

    protected ListComponent<TestCase> getTable() {
        return (ListComponent<TestCase>) table;
    }

    protected ListComponent<Step> getStepsTable() {
        return (ListComponent<Step>) stepsTable;
    }

    protected GridLayout getGrid() {
        return (GridLayout) getWindow().getComponentNN("caseForm");
    }

    protected void initMasterDetailScreen(@SuppressWarnings("unused") InitEvent event) {

        initDataComponents();
        initOkCancelActions();
        initBrowseItemChangeListener();
        initBrowseCreateAction();
        initBrowseEditAction();
        disableEditControls();
    }

    protected void initDataComponents() {
        CollectionContainer<TestCase> browseContainer = getBrowseContainer();
        DataLoader browseLoader = ((HasLoader) browseContainer).getLoader();
        if (browseLoader != null) {
            browseLoader.setDataContext(null);
        }
    }

    protected CollectionContainer<TestCase> getBrowseContainer() {
        DataUnit items = getTable().getItems();
        if (items instanceof ContainerDataUnit)
            return ((ContainerDataUnit<TestCase>) items).getContainer();
        else
            throw new UnsupportedOperationException("Unsupported items: " + items);
    }

    protected void initOkCancelActions() {
        ((BaseAction) getWindow().getActionNN("save")).withHandler(actionPerformedEvent -> saveChanges());
        ((BaseAction) getWindow().getActionNN("cancel")).withHandler(actionPerformedEvent -> discardChanges());
    }

    protected void saveChanges() {
        if (!editing) {
            return;
        }

        TestCase editedItem = testCaseDc.getItem();
        if (creating) {
            ValidationErrors errors = screenValidation.validateUiComponents(getGrid());
            if (!errors.isEmpty()) {
                screenValidation.showValidationErrors(this, errors);
                return;
            }
            getBrowseContainer().getMutableItems().add(editedItem);
        } else {
            ValidationErrors errors = screenValidation.validateUiComponents(getGrid());
            if (!errors.isEmpty()) {
                screenValidation.showValidationErrors(this, errors);
                return;
            }
            getBrowseContainer().replaceItem(editedItem);
        }
        getTable().setSelected(editedItem);

        releaseLock();
        disableEditControls();
    }

    @Subscribe("table.edit")
    public void onTableEdit(Action.ActionPerformedEvent event) {
        try {
            TestCase selectedItem = getBrowseContainer().getItemOrNull();
            if (selectedItem != null && PersistenceHelper.isNew(selectedItem)) {
                tempCase = (TestCase) selectedItem.clone();
            }
        } catch (CloneNotSupportedException ex) {
            return;
        }
    }

    protected void discardChanges() {
        releaseLock();
        getScreenData().getDataContext().evictModified();
        testCaseDc.setItem(null);
        TestCase selectedItem = getBrowseContainer().getItemOrNull();
        if (selectedItem != null) {
            if (PersistenceHelper.isNew(selectedItem)) {
                getBrowseContainer().replaceItem(tempCase);
                testCaseDc.setItem(tempCase);
            } else {
                View view = testCaseDc.getView();
                boolean loadDynamicAttributes = getEditLoader().isLoadDynamicAttributes();
                TestCase reloadedItem = getBeanLocator().get(DataManager.class)
                        .reload(selectedItem, view, null, loadDynamicAttributes);
                getBrowseContainer().replaceItem(reloadedItem);
                testCaseDc.setItem(reloadedItem);
            }
        }

        disableEditControls();
    }

    protected InstanceLoader<TestCase> getEditLoader() {
        DataLoader loader = ((HasLoader) testCaseDc).getLoader();
        if (loader == null) {
            throw new IllegalStateException("Cannot find loader of editing container");
        }
        return (InstanceLoader<TestCase>) loader;
    }

    protected void initBrowseItemChangeListener() {
        getBrowseContainer().addItemChangeListener(e -> {
            if (!editing) {
                testCaseDc.setItem(e.getItem());
            }
        });
    }

    @Subscribe("caseResult")
    public void onCaseResultValueChange1(HasValue.ValueChangeEvent<CaseResult> event) {
        if (caseResult.getValue() != null
                && caseResult.getValue().equals(CaseResult.FAILED)) {
            ticketBox.setVisible(true);
            caseComment.setVisible(true);
            checkDate.setVisible(false);

        } else if (caseResult.getValue() != null &&
                caseResult.getValue().equals(CaseResult.PASSED)) {
            checkDate.setVisible(true);
            ticketBox.setVisible(false);
            caseComment.setVisible(false);

            if (testCaseDc.getItem().getCheckDate() == null) {
                checkDate.setValue(timeSource.now().toLocalDateTime());
            }

        } else {
            ticketBox.setVisible(false);
            caseComment.setVisible(false);
            checkDate.setVisible(false);
        }
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

    @Install(to = "table.remove", subject = "afterActionPerformedHandler")
    private void tableRemoveAfterActionPerformedHandler(RemoveOperation.AfterActionPerformedEvent<TestCase> afterActionPerformedEvent) {
        List<TestCase> cases = testCasesDc.getMutableItems();
        for (int i = 0; i < cases.size(); i++) {
            cases.get(i).setNumber(i + 1);
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

    protected void initBrowseCreateAction() {
        ListComponent<TestCase> table = getTable();
        CreateAction createAction = (CreateAction) table.getActionNN("create");
        createAction.withHandler(actionPerformedEvent -> {
            TestCase entity = getBeanLocator().get(Metadata.class).create(getEntityClass());
            entity.setChecklist(getEditedEntityContainer().getItem());
            entity.setCreationDate(timeSource.currentTimestamp());

            if (getEditedEntityContainer().getItem().getTestCase() == null) {
                entity.setNumber(1);
            } else {
                int lastNum = 0;
                for (TestCase tc : getEditedEntityContainer().getItem().getTestCase()) {
                    if (tc.getNumber() > lastNum) {
                        lastNum = tc.getNumber();
                    }
                }
                entity.setNumber(lastNum + 1);
            }

            TestCase trackedEntity = getScreenData().getDataContext().merge(entity);

            DynamicAttributesGuiTools tools = getBeanLocator().get(DynamicAttributesGuiTools.NAME);
            String screenId = UiControllerUtils.getScreenContext(this)
                    .getWindowInfo().getId();

            InstanceLoader<TestCase> instanceLoader = null;
            InstanceContainer<TestCase> editedEntityContainer = testCaseDc;
            if (editedEntityContainer instanceof HasLoader) {
                if (((HasLoader) editedEntityContainer).getLoader() instanceof InstanceLoader) {
                    instanceLoader = getEditLoader();
                    if (tools.screenContainsDynamicAttributes(editedEntityContainer.getView(), screenId)) {
                        instanceLoader.setLoadDynamicAttributes(true);
                    }
                }
            }

            if (instanceLoader != null
                    && instanceLoader.isLoadDynamicAttributes()
                    && trackedEntity instanceof BaseGenericIdEntity) {
                tools.initDefaultAttributeValues((BaseGenericIdEntity<UUID>) trackedEntity, trackedEntity.getMetaClass());
            }

            fireEvent(MasterDetailScreen.InitEntityEvent.class, new MasterDetailScreen.InitEntityEvent<>(this, trackedEntity));

            testCaseDc.setItem(trackedEntity);
            refreshOptionsForLookupFields();
            enableEditControls(true);
            table.setSelected(Collections.emptyList());
//            stepsTable.focus();
//            Step step = testCaseDc.getItem().getCaseStep().get(0);
//            stepsTable.setSelected(step);
        });
    }

    protected void refreshOptionsForLookupFields() {
        for (Component component : getGrid().getOwnComponents()) {
            if (component instanceof LookupField) {
                Options options = ((LookupField) component).getOptions();
                if (options instanceof ContainerOptions) {
                    CollectionContainer container = ((ContainerOptions) options).getContainer();
                    if (container instanceof HasLoader) {
                        DataLoader optionsLoader = ((HasLoader) container).getLoader();
                        if (optionsLoader != null && DataLoadersHelper.areAllParametersSet(optionsLoader)) {
                            optionsLoader.load();
                        }
                    }
                }
            }
        }
    }

    protected void enableEditControls(boolean creating) {
        this.editing = true;
        this.creating = creating;
        initEditComponents(true);
        getGrid().focusFirstComponent();
    }

    protected void initEditComponents(boolean enabled) {
        for (Component component : getVBox().getComponents()) {
            if (component instanceof Component.Editable)
                ((Component.Editable) component).setEditable(enabled);
        }
        getVBox().setEnabled(enabled);

        getActionsPane().setVisible(enabled);
        getLookupBox().setEnabled(!enabled);

    }

    @Subscribe("table")
    public void onTableSelection(Table.SelectionEvent<TestCase> event) {
        caseNameField.setStyleName("bright-disabled");
        initialConditions.setStyleName("bright-disabled");
        caseHours.setStyleName("bright-disabled");
        caseMinutes.setStyleName("bright-disabled");
        expectedResultField.setStyleName("bright-disabled");
        caseComment.setStyleName("bright-disabled");
    }

    protected ComponentContainer getLookupBox() {
        return (ComponentContainer) getWindow().getComponentNN("lookupBox");
    }

    protected ComponentContainer getVBox() {
        return (ComponentContainer) getWindow().getComponentNN("editBox");
    }

    protected ComponentContainer getActionsPane() {
        return (ComponentContainer) getWindow().getComponentNN("actionsPane");
    }

    protected Class<TestCase> getEntityClass() {
        return getBrowseContainer().getEntityMetaClass().getJavaClass();
    }

    protected void initBrowseEditAction() {
        ListComponent<TestCase> table = getTable();
        EditAction editAction = (EditAction) table.getActionNN("edit");
        editAction.withHandler(actionPerformedEvent -> {
            TestCase item = table.getSingleSelected();
            if (item != null) {
                if (lockIfNeeded(item)) {
                    refreshOptionsForLookupFields();
                    enableEditControls(false);
                }
            }
        });
        editAction.addEnabledRule(() ->
                table.getSelected().size() == 1
                        && getBeanLocator().get(Security.class).isEntityOpPermitted(getEntityClass(), EntityOp.UPDATE));
    }

    protected boolean lockIfNeeded(Entity<UUID> entity) {
        LockService lockService = getBeanLocator().get(LockService.class);

        LockInfo lockInfo = lockService.lock(getLockName(), entity.getId().toString());
        if (lockInfo == null) {
            justLocked = true;
        } else if (!(lockInfo instanceof LockNotSupported)) {
            Messages messages = getBeanLocator().get(Messages.class);
            DatatypeFormatter datatypeFormatter = getBeanLocator().get(DatatypeFormatter.class);
            Notifications notifications = UiControllerUtils.getScreenContext(this)
                    .getNotifications();

            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messages.getMainMessage("entityLocked.msg"))
                    .withDescription(String.format(messages.getMainMessage("entityLocked.desc"),
                            lockInfo.getUser().getLogin(),
                            datatypeFormatter.formatDateTime(lockInfo.getSince())))
                    .show();

            return false;
        }
        return true;
    }

    protected void disableEditControls() {
        this.editing = false;
        initEditComponents(false);
        ((Component.Focusable) getTable()).focus();
    }

    protected OperationResult commitEditorChanges() {
        ValidationErrors validationErrors = validateEditorForm();
        if (!validationErrors.isEmpty()) {
            ScreenValidation screenValidation = getBeanLocator().get(ScreenValidation.class);
            screenValidation.showValidationErrors(this, validationErrors);
            return OperationResult.fail();
        }

        Runnable standardCommitAction = () -> {
            getScreenData().getDataContext().commit();
            fireEvent(MasterDetailScreen.AfterCommitChangesEvent.class, new MasterDetailScreen.AfterCommitChangesEvent(this));
        };

        MasterDetailScreen.BeforeCommitChangesEvent beforeEvent = new MasterDetailScreen.BeforeCommitChangesEvent(this, standardCommitAction);
        fireEvent(MasterDetailScreen.BeforeCommitChangesEvent.class, beforeEvent);

        if (beforeEvent.isCommitPrevented()) {
            if (beforeEvent.getCommitResult() != null) {
                return beforeEvent.getCommitResult();
            }

            return OperationResult.fail();
        }

        standardCommitAction.run();

        return OperationResult.success();
    }

    protected ValidationErrors validateEditorForm() {
        ValidationErrors validationErrors = validateUiComponents();

        validateAdditionalRules(validationErrors);

        return validationErrors;
    }

    public void setQaParameter(List<QaProjectRelationship> qaProjectRelationship) {
        assignedQaField.setOptionsList(qaProjectRelationship);
    }

    public void setModuleParameter(List<Module> modules) {
        moduleField.setOptionsList(modules);
    }

    @Override
    protected boolean doNotReloadEditedEntity() {
        return true;
    }

    @Subscribe(id = "checklistDc", target = Target.DATA_CONTAINER)
    public void onChecklistDcItemChange(InstanceContainer.ItemChangeEvent<Checklist> event) {
        List<Step> stepsList = new ArrayList<>();
        if (event.getItem().getTestCase() != null) {
            for (TestCase tc : event.getItem().getTestCase()) {
                if (tc.getCaseStep() != null) {
                    stepsList.addAll(tc.getCaseStep());
                }
            }
        }
        entityLogItemsDl.setParameter("checklist", Objects.requireNonNull(event.getItem()).getId());
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
        if (!event.getProperty().equals("result")) {
            return;
        }

        Statement completedState = dataManager.load(Statement.class)
                .id(UUID.fromString("d9d8fd34-068d-99db-5adc-9d95731bc419")).one();

        boolean hasUnchecked = false;
        for (TestCase tc : testCasesDc.getItems()) {
            if (tc.getResult() == null ||
                    !tc.getResult().equals(CaseResult.PASSED) &&
                            !tc.getResult().equals(CaseResult.SKIPPED)) {
                hasUnchecked = true;
            }
        }

        if (!hasUnchecked) {
            stateField.setValue(completedState);
        }
    }

    @Subscribe("stateField")
    public void onStateFieldValueChange(HasValue.ValueChangeEvent<Statement> event) {
        if (event.getValue() != null &&
                event.getValue().getId().equals(UUID.fromString("d9d8fd34-068d-99db-5adc-9d95731bc419"))) {
            for (TestCase tc : testCasesDc.getMutableItems()) {
                if (tc.getResult() == null ||
                        !tc.getResult().equals(CaseResult.PASSED) &&
                                !tc.getResult().equals(CaseResult.SKIPPED)) {
                    tc.setResult(CaseResult.PASSED);
                    tc.setCheckDate(timeSource.now().toLocalDateTime());
                }
            }
        }
    }

    @Install(to = "caseResult", subject = "optionStyleProvider")
    protected String caseResultOptionStyleProvider(CaseResult caseResult) {
        if (caseResult != null) {
            switch (caseResult) {
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

    @Subscribe("caseResult")
    public void onCaseResultValueChange(HasValue.ValueChangeEvent<CaseResult> event) {
        if (event.getValue() != null) {
            switch (event.getValue()) {
                case PASSED:
                    caseResult.setStyleName("passed-result");
                    return;
                case FAILED:
                    caseResult.setStyleName("failed-result");
                    return;
                case BLOCKED:
                    caseResult.setStyleName("blocked-result");
                    return;
                case SKIPPED:
                    caseResult.setStyleName("skipped-result");
                    return;
            }
        }
    }
}