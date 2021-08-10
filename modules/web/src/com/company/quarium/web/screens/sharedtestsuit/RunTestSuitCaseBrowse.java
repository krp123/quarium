package com.company.quarium.web.screens.sharedtestsuit;

import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.entity.testsuit.TestSuit;
import com.company.quarium.web.screens.testcase.RunTestCaseBrowserFrame;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UiController("quarium_RunTestSuitCase.browse")
@UiDescriptor("run-test-suit-case-browse.xml")
@LookupComponent("testSuitsTable")
@LoadDataBeforeShow
public class RunTestSuitCaseBrowse extends StandardLookup<TestSuit> {
    @Inject
    private CollectionLoader<TestSuit> testSuitsDl;
    @Inject
    private RunTestCaseBrowserFrame casesFragment;
    @Inject
    private TabSheet tabsheet;
    @Inject
    private GroupTable<TestSuit> testSuitsTable;
    @Inject
    private Button selectButton;
    @Inject
    private DataManager dataManager;
    @Inject
    private CollectionContainer<TestSuit> testSuitsDc;

    @Subscribe
    public void onInit(InitEvent event) {
        ScreenOptions options = event.getOptions();
        if (options instanceof MapScreenOptions) {
            Project project = (Project) ((MapScreenOptions) options).getParams().get("project");
            testSuitsDl.setParameter("project", project);
            casesFragment.setProjectParameter(project);
//            testCasesTab.add(casesFragment.getFragment());
        }
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

        Table testCasesTable = (Table) casesFragment.getFragment().getComponent("testCasesTable");
        tabsheet.addSelectedTabChangeListener(selectedTabChangeEvent -> {
            if (!testCasesTable.getLookupSelectedItems().isEmpty()) {
                testCasesTable.setSelected(new ArrayList<>());
                testSuitsTable.setSelected(new ArrayList<>());
                testSuitsDl.load();
            }
        });

        testCasesTable.addSelectionListener(selectionEvent -> {
            Collection lookupSelectedItems = testCasesTable.getLookupSelectedItems();
            List newTestSuitList = new ArrayList();
            if (!lookupSelectedItems.isEmpty()) {
                newTestSuitList = createTestSuitList(lookupSelectedItems);
            }
            testSuitsTable.setSelected(newTestSuitList);
            selectButton.setEnabled(true);
        });
    }

    private List<TestSuit> createTestSuitList(Collection<TestCase> testCaseList) {

        List<TestSuit> testSuitList = new ArrayList<>();

        for (TestCase testCase : testCaseList) {

            String testSuitName = testCase.getTestSuit().getName();
            TestSuit newTestSuit = dataManager.create(TestSuit.class);
            newTestSuit.setName(testSuitName);
            newTestSuit.setMinutes(testCase.getTestSuit().getMinutes());
            newTestSuit.setHours(testCase.getTestSuit().getHours());
            newTestSuit.setModule(testCase.getTestSuit().getModule());
            newTestSuit.setComment(testCase.getTestSuit().getComment());
            newTestSuit.setTicket(testCase.getTestSuit().getTicket());
            newTestSuit.setInitialConditions(testCase.getTestSuit().getInitialConditions());

            if (!checkIfTestSuitExist(testSuitList, testSuitName)) {

                List<TestCase> newTestCaseList = new ArrayList<>();
                newTestCaseList.add(testCase);
                newTestSuit.setTestCase(newTestCaseList);
                testSuitList.add(newTestSuit);

            } else {
                TestSuit testSuitByName = getTestSuitByName(testSuitList, testSuitName);
                testSuitByName.getTestCase().add(testCase);
            }
            testSuitsDc.getMutableItems().add(newTestSuit);
        }

        return testSuitList;
    }

    private boolean checkIfTestSuitExist(List<TestSuit> list, String name) {
        for (TestSuit testSuit : list) {
            if (name.equals(testSuit.getName())) {
                return true;
            }
        }
        return false;
    }

    private TestSuit getTestSuitByName(List<TestSuit> list, String name) {
        for (TestSuit testSuit : list) {
            if (name.equals(testSuit.getName())) {
                return testSuit;
            }
        }
        return null;
    }
}