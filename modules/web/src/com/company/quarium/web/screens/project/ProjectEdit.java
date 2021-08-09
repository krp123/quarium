package com.company.quarium.web.screens.project;

import com.company.quarium.entity.project.*;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.entity.testsuit.CaseStatus;
import com.company.quarium.entity.testsuit.SharedTestSuit;
import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.entity.testsuit.TestSuit;
import com.company.quarium.service.CopyTestSuitService;
import com.company.quarium.web.screens.testsuit.ProjectExcelUploadWindow;
import com.company.quarium.web.screens.testsuit.ProjectTestSuitEdit;
import com.company.quarium.web.screens.testrun.TestRunEdit;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UiController("quarium_SimpleProject.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
@LoadDataBeforeShow
public class ProjectEdit extends StandardEditor<Project> {

    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected DataContext dataContext;
    @Inject
    protected CollectionContainer<QaProjectRelationship> qaProjectDc;
    @Inject
    protected CollectionContainer<ConfigurationProjectRelationship> configurationProjectDc;
    @Inject
    protected InstanceContainer<SimpleProject> projectDc;
    @Inject
    private CollectionContainer<SharedTestSuit> testSuitsDc;
    @Inject
    private DataManager dataManager;
    @Inject
    private CopyTestSuitService copyTestSuitService;
    @Inject
    private CollectionLoader<QaProjectRelationship> qaDl;
    @Inject
    private TimeSource timeSource;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Button saveBtn;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;
    @Inject
    private CollectionLoader<TestSuit> testSuitsFilterDl;
    @Inject
    private PopupButton createPopup;
    @Inject
    private GroupTable<SharedTestSuit> testSuitsTable;
    @Inject
    private GroupTable<TestRun> testRunsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private EntityStates entityStates;
    @Inject
    private Dialogs dialogs;
    @Inject
    private CollectionLoader<TestRun> testRunsDl;

    @Subscribe("testRunCreateBtn")
    public void onTestRunCreateBtnClick(Button.ClickEvent event) {
        if (entityStates.isNew(getEditedEntity())) {
            dialogs.createOptionDialog()
                    .withCaption(messages.getMessage(getClass(), "attention"))
                    .withMessage(messages.getMessage(getClass(), "createRunAttentionMessage"))
                    .withActions(
                            new DialogAction(DialogAction.Type.YES)
                                    .withHandler(e -> {
                                        commitChanges();
                                        if (!entityStates.isNew(getEditedEntity())) {
                                            buildAndShowTestRunEditor();
                                        }
                                    }),
                            new DialogAction(DialogAction.Type.NO)
                    )
                    .show();
        } else {
            buildAndShowTestRunEditor();
        }
    }

    private void buildAndShowTestRunEditor() {
        TestRun newTestRun = dataManager.create(TestRun.class);
        newTestRun.setProject(getEditedEntity());
        TestRunEdit testRunEdit = screenBuilders.editor(TestRun.class, this)
                .withScreenClass(TestRunEdit.class)
                .withLaunchMode(OpenMode.THIS_TAB)
                .newEntity(newTestRun)
                .build();
        testRunEdit.setProjectParameter(getEditedEntity());
        testRunEdit.addAfterCloseListener(afterShowEvent -> {
            testRunsDl.load();
            testRunsTable.repaint();
        });
        testRunEdit.show();
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<Project> event) {
        event.getEntity().setCreationDate(timeSource.currentTimestamp());
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        qaDl.setParameter("project", getEditedEntity());
        testRunsDl.setParameter("project", getEditedEntity());
        testSuitsFilterDl.setParameter("project", getEditedEntity());
    }

    @Subscribe("qaProjectRelationshipsTable.addQa")
    protected void onAddQa(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Qa.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(qas -> {
                    qas.stream()
                            .filter(qa -> {
                                //если датасорс пустой, то добавляем всех
                                if (qaProjectDc.getItems().isEmpty()) {
                                    return true;
                                }

                                //проверяем, если хоть в одной из записей есть QA, то возвращаем false
                                boolean hasQa;
                                for (QaProjectRelationship qpr : qaProjectDc.getItems()) {
                                    hasQa = qpr.getQa().equals(qa);
                                    if (hasQa)
                                        return false;
                                }
                                return true;
                            })
                            .map(this::createRelationshipFromQa)
                            .forEach(this::addQaToRelationships);
                })
                .build()
                .show();
    }


    @Subscribe("configurationProjectRelationshipsTable.addConfiguration")
    protected void onAddConfiguration(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Configuration.class, this)
                .withLaunchMode(OpenMode.DIALOG)
                .withSelectHandler(configurations -> {
                    configurations.stream()
                            .map(this::createRelationshipFromConfiguration)
                            .forEach(this::addConfigurationToRelationships);
                })
                .build()
                .show();
    }

    @Subscribe("testSuitsTable.addTestSuit")
    protected void onAddTestSuit(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(TestSuit.class, this)
                .withLaunchMode(OpenMode.THIS_TAB)
                .withScreenId("quarium_TestSuitCase.browse")
                .withSelectHandler(checklists -> {
                    checklists.stream()
                            .forEach(this::createAndAddTestSuit);
                })
                .build()
                .show();
    }

    private QaProjectRelationship createRelationshipFromQa(Qa qa) {
        QaProjectRelationship qaProjectRelationship = dataContext.create(QaProjectRelationship.class);
        qaProjectRelationship.setProject(projectDc.getItem());
        qaProjectRelationship.setQa(qa);

        return qaProjectRelationship;
    }

    private ConfigurationProjectRelationship createRelationshipFromConfiguration(Configuration configuration) {
        ConfigurationProjectRelationship configurationProjectRelationship =
                dataContext.create(ConfigurationProjectRelationship.class);
        configurationProjectRelationship.setProject(projectDc.getItem());
        configurationProjectRelationship.setConfiguration(configuration);

        return configurationProjectRelationship;
    }

    private TestSuit createAndAddTestSuit(TestSuit testSuit) {
        try {
            testSuit = dataManager.load(TestSuit.class).id(testSuit.getId()).view("project-testSuit-view").one();
        }catch (IllegalStateException e){

        }
        SharedTestSuit testSuitNew = copyTestSuitService.copyTestSuit(testSuit);
        testSuitNew.setProject(projectDc.getItem());
        testSuitsDc.getMutableItems().add(testSuitNew);
        return testSuitNew;
    }

    private void addQaToRelationships(QaProjectRelationship qaProjectRelationship) {
        qaProjectDc.getMutableItems().add(qaProjectRelationship);
    }

    private void addConfigurationToRelationships(ConfigurationProjectRelationship configurationProjectRelationship) {
        configurationProjectDc.getMutableItems().add(configurationProjectRelationship);
    }

//    @Install(to = "testRunsTable.createRun", subject = "screenConfigurer")
//    protected void testRunsTableCreateScreenConfigurer(Screen editorScreen) {
//        ((TestRunEdit) editorScreen).setProjectParameter(getEditedEntity());
//    }

    @Install(to = "testRunsTable.edit", subject = "screenConfigurer")
    protected void testRunsTableEditScreenConfigurer(Screen editorScreen) {
        ((TestRunEdit) editorScreen).setProjectParameter(getEditedEntity());
    }

    @Install(to = "testSuitsTable.edit", subject = "screenConfigurer")
    protected void testSuitsTableEditScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((ProjectTestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((ProjectTestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

    @Install(to = "testSuitsTable.create", subject = "screenConfigurer")
    protected void testSuitsTableCreateScreenConfigurer(Screen editorScreen) {
        if (getEditedEntity().getQa() != null) {
            ((ProjectTestSuitEdit) editorScreen).setQaParameter(getEditedEntity().getQa());
        }

        if (getEditedEntity().getModule() != null) {
            ((ProjectTestSuitEdit) editorScreen).setModuleParameter(getEditedEntity().getModule());
        }
    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        saveBtn.setAction(new AbstractAction("saveCard") {
            @Override
            public void actionPerform(Component component) {
                commitChanges();
                notifications.create()
                        .withCaption(messages.getMessage(getClass(), "saveNotification"))
                        .show();
            }
        });

        if (userSessionSource.getUserSession().getRoles().contains("View")) {
            createPopup.setVisible(false);
            saveBtn.setVisible(false);
        }

        testRunsTable.addGeneratedColumn("completedPercent",
                new Table.ColumnGenerator<TestRun>() {
                    @Override
                    public Component generateCell(TestRun testRun) {
                        Label label = uiComponents.create(Label.NAME);

                        List<TestCase> passedCases = new ArrayList<>();
                        for (TestSuit ts : testRun.getChecklists()) {
                            if (ts.getTestCase() != null) {
                                for (TestCase tc : ts.getTestCase()) {
                                    if (tc.getStatus() != null
                                            && tc.getStatus().equals(CaseStatus.PASSED)) {
                                        passedCases.add(tc);
                                    }
                                }
                            }
                        }
                        double passedCasesSize = passedCases.size();

                        List<TestCase> allCases = new ArrayList<>();
                        for (TestSuit ts : testRun.getChecklists()) {
                            if (ts.getTestCase() != null) {
                                allCases.addAll(ts.getTestCase());
                            }
                        }

                        String percentCompleted = "0%";

                        if (!passedCases.isEmpty()) {
                            percentCompleted = String.format("%.2f", passedCasesSize / allCases.size() * 100) + "%";
                        }
                        label.setValue(percentCompleted);
                        return label;
                    }
                });
    }

    @Subscribe("createPopup.copy")
    public void onCreatePopupCopy(Action.ActionPerformedEvent event) {
        if (testSuitsTable.getSingleSelected() != null) {
            SharedTestSuit newCl = copyTestSuitService.copyTestSuit(testSuitsTable.getSingleSelected());
            newCl.setProject(getEditedEntity());

            screenBuilders.editor(testSuitsTable)
                    .withScreenId("quarium_ProjectTestSuit.edit")
                    .editEntity(newCl)
                    .build()
                    .show();
        }
    }

    @Subscribe("createPopup.uploadExcel")
    public void onCreatePopupUploadExcel(Action.ActionPerformedEvent event) {
        ProjectExcelUploadWindow uploadWindow = screenBuilders.screen(this)
                .withScreenClass(ProjectExcelUploadWindow.class)
                .build();
        uploadWindow.setTestSuitsDc(testSuitsDc);
        uploadWindow.setProject((SimpleProject) getEditedEntity());
        uploadWindow.show();
    }

    @Subscribe(id = "testSuitsDc", target = Target.DATA_CONTAINER)
    private void onChecklistsDcCollectionChange(
            CollectionContainer.CollectionChangeEvent<SharedTestSuit> event) {
        CollectionChangeType changeType = event.getChangeType();
        Collection<? extends SharedTestSuit> changes = event.getChanges();
    }
}