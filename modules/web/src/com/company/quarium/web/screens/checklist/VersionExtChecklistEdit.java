package com.company.quarium.web.screens.checklist;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.Step;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.haulmont.cuba.core.app.LockService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.DataUnit;
import com.haulmont.cuba.gui.components.data.Options;
import com.haulmont.cuba.gui.components.data.meta.ContainerDataUnit;
import com.haulmont.cuba.gui.components.data.options.ContainerOptions;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.util.OperationResult;

import javax.inject.Inject;
import java.util.List;

@UiController("quarium_VersionExtChecklist.edit")
@UiDescriptor("version-ext-checklist-edit.xml")
@EditedEntityContainer("checklistDc")
@LoadDataBeforeShow
public class VersionExtChecklistEdit extends StandardEditor<Checklist> {

    protected boolean editing;
    protected boolean creating;
    protected boolean justLocked;

    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    private GroupTable<TestCase> table;
    @Inject
    private Table<Step> stepsTable;
    @Inject
    private LookupField<QaProjectRelationship> assignedQaField;
    @Inject
    private LookupField<Module> moduleField;
    @Inject
    private TimeSource timeSource;
    @Inject
    private ScreenValidation screenValidation;
    @Inject
    protected CheckBox isUsedInRegress;


    @Subscribe
    protected void onInit(InitEvent event) {
        initMasterDetailScreen(event);
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
        ((BaseAction) getWindow().getActionNN("cancel")).withHandler(actionPerformedEvent -> discardChanges());
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