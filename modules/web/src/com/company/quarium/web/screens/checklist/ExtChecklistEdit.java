package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.Step;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Statement;
import com.company.quarium.web.gui.components.LinkField;
import com.haulmont.cuba.core.app.EntitySnapshotService;
import com.haulmont.cuba.core.app.LockService;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
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
import com.haulmont.cuba.security.entity.EntityOp;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

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
    private CollectionContainer<Step> stepsCollection;
    @Inject
    private GroupTable<TestCase> table;
    @Inject
    private Table<Step> stepsTable;
    @Inject
    private InstanceContainer<Checklist> checklistDc;
    @Inject
    protected EntitySnapshotService entitySnapshotService;
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;
    @Inject
    private DataManager dataManager;
    @Inject
    private TimeSource timeSource;
    @Inject
    private LinkField caseTicket;
    @Inject
    private TextArea<String> caseComment;
    @Inject
    private LookupField<Statement> caseStateField;
    @Inject
    private InstanceLoader<TestCase> testCaseDl;
    @Inject
    private HBoxLayout ticketBox;


    @Subscribe
    protected void onInit(InitEvent event) {
        initMasterDetailScreen(event);
    }

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        entitySnapshotService.createSnapshot(checklistDc.getItem(), checklistDc.getView());
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
            getBrowseContainer().getMutableItems().add(0, editedItem);
        } else {
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

    @Subscribe("caseStateField")
    public void onCaseStateFieldValueChange(HasValue.ValueChangeEvent<Statement> event) {
        if (caseStateField.getValueSource().getValue() != null
                && caseStateField.getValueSource().getValue().getId().toString().equals("cd85906d-6fbe-3e8d-8602-bf1af8e1ea53")) {
            ticketBox.setVisible(true);
            caseComment.setVisible(true);
        } else {
            ticketBox.setVisible(false);
            caseComment.setVisible(false);
        }
    }

    @Subscribe("stepsTable.createStep")
    public void onCreateStep(Action.ActionPerformedEvent event) {
        Step entity = dataManager.create(Step.class);
        entity.setTestCase(testCaseDc.getItem());
        entity.setCreationDate(timeSource.currentTimestamp());
        stepsCollection.getMutableItems().add(entity);
    }


    protected void initBrowseCreateAction() {
        ListComponent<TestCase> table = getTable();
        CreateAction createAction = (CreateAction) table.getActionNN("create");
        createAction.withHandler(actionPerformedEvent -> {
            TestCase entity = getBeanLocator().get(Metadata.class).create(getEntityClass());
            entity.setChecklist(getEditedEntityContainer().getItem());
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
}