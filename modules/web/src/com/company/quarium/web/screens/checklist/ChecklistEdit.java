package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.Step;
import com.company.quarium.entity.checklist.TestCase;
import com.haulmont.cuba.core.app.EntitySnapshotService;
import com.haulmont.cuba.core.app.LockService;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.RemoveOperation;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.actions.list.EditAction;
import com.haulmont.cuba.gui.app.core.entitydiff.EntityDiffViewer;
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
import com.haulmont.cuba.security.entity.EntityOp;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@UiController("quarium_SimpleChecklist.edit")
@UiDescriptor("checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class ChecklistEdit extends StandardEditor<Checklist> {

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
    private InstanceContainer<Checklist> checklistDc;
    @Inject
    protected EntitySnapshotService entitySnapshotService;
    @Inject
    private EntityStates entityStates;
    @Inject
    protected EntityDiffViewer diffFrame;
    @Inject
    protected TimeSource timeSource;
    @Inject
    private DataManager dataManager;
    @Inject
    private ScreenValidation screenValidation;
    @Inject
    private Button removeStep;

    @Subscribe
    protected void onInit(InitEvent event) {
        initMasterDetailScreen(event);
    }

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        entitySnapshotService.createSnapshot(checklistDc.getItem(), checklistDc.getView());
    }

    @Subscribe
    protected void afterInit(AfterShowEvent event) {
        if (!entityStates.isNew(checklistDc.getItem())) {
            diffFrame.loadVersions(checklistDc.getItem());
        }
    }

    protected ListComponent<TestCase> getTable() {
        return (ListComponent) table;
    }

    protected ListComponent<Step> getStepsTable() {
        return (ListComponent) stepsTable;
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

    protected void discardChanges() {
        releaseLock();
        getScreenData().getDataContext().evictModified();
        testCaseDc.setItem(null);

        TestCase selectedItem = getBrowseContainer().getItemOrNull();
        if (selectedItem != null) {
            View view = testCaseDc.getView();
            boolean loadDynamicAttributes = getEditLoader().isLoadDynamicAttributes();
            TestCase reloadedItem = getBeanLocator().get(DataManager.class)
                    .reload(selectedItem, view, null, loadDynamicAttributes);
            getBrowseContainer().replaceItem(reloadedItem);
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

    @Subscribe("stepsTable.createStep")
    public void onCreateStep(Action.ActionPerformedEvent event) {
        Step entity = dataManager.create(Step.class);
        entity.setTestCase(testCaseDc.getItem());
        if (testCaseDc
                .getItem()
                .getCaseStep() == null) {
            entity.setNumber(1);
        } else {
            int lastNum = 0;
            for (Step s : testCaseDc.getItem().getCaseStep()) {
                if (s.getNumber() > lastNum) {
                    lastNum = s.getNumber();
                }
            }
            entity.setNumber(lastNum + 1);
        }
        stepsCollection.getMutableItems().add(entity);
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

    protected void initBrowseCreateAction() {
        ListComponent<TestCase> table = getTable();
        CreateAction createAction = (CreateAction) table.getActionNN("create");
        createAction.withHandler(actionPerformedEvent -> {
            TestCase entity = getBeanLocator().get(Metadata.class).create(getEntityClass());
            entity.setChecklist(getEditedEntityContainer().getItem());

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

            entity.setCreationDate(timeSource.currentTimestamp());
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
                tools.initDefaultAttributeValues((BaseGenericIdEntity) trackedEntity, trackedEntity.getMetaClass());
            }

            fireEvent(MasterDetailScreen.InitEntityEvent.class, new MasterDetailScreen.InitEntityEvent<>(this, trackedEntity));

            testCaseDc.setItem(trackedEntity);
            refreshOptionsForLookupFields();
            enableEditControls(true);
            table.setSelected(Collections.emptyList());
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

    protected boolean lockIfNeeded(Entity entity) {
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

        MasterDetailScreen.BeforeCommitChangesEvent beforeEvent =
                new MasterDetailScreen.BeforeCommitChangesEvent(this, standardCommitAction);
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
}