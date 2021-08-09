package com.company.quarium.web.screens.testsuit;

import com.company.quarium.entity.testsuit.TestCase;
import com.company.quarium.entity.testsuit.TestSuit;
import com.company.quarium.web.screens.testcase.ProjectTestCaseBrowserFrame;
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
import java.util.Map;

@UiController("quarium_TestSuitCase.browse")
@UiDescriptor("test-suit-case-browse.xml")
@LookupComponent("testSuitsTable")
@LoadDataBeforeShow
public class TestSuitCaseBrowse extends StandardLookup<TestSuit> {

    @Inject
    private CollectionLoader<TestSuit> testSuitsDl;
    @Inject
    private TabSheet tabsheet;

    @Inject
    private ProjectTestCaseBrowserFrame casesFragment;

    @Inject
    private Button selectButton;

    @Inject
    private GroupTable<TestSuit> testSuitsTable;

    @Inject
    private DataManager dataManager;

    @Inject
    private CollectionContainer<TestSuit> testSuitsDc;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

        Map<String, Object> params = this.getWindow().getContext().getParams();
        if (hasProjectParameter(params)) {
            findTestSuitByProject(params);
        }


        Table testCasesTable = (Table) casesFragment.getFragment().getComponent("testCasesTable");
        tabsheet.addSelectedTabChangeListener(selectedTabChangeEvent -> {
            if (!testCasesTable.getLookupSelectedItems().isEmpty()) {
                testCasesTable.setSelected(new ArrayList<>());
                testSuitsTable.setSelected(new ArrayList<>());
                if (hasProjectParameter(params)) {
                    findTestSuitByProject(params);
                } else {
                    testSuitsDl.load();
                }
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
            testSuitsDc.getMutableItems().add(newTestSuit);

            if (!checkIfTestSuitExist(testSuitList, testSuitName)) {

                List<TestCase> newTestCaseList = new ArrayList<>();
                newTestCaseList.add(testCase);
                newTestSuit.setTestCase(newTestCaseList);
                testSuitList.add(newTestSuit);

            } else {
                TestSuit testSuitByName = getTestSuitByName(testSuitList, testSuitName);
                testSuitByName.getTestCase().add(testCase);
            }
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

    private boolean hasProjectParameter(Map<String, Object> params) {
        if (params.containsKey("project")) {
            return true;
        }
        return false;
    }

    private void findTestSuitByProject(Map<String, Object> params) {
        List<TestSuit> testSuits = dataManager.load(TestSuit.class)
                .query("select e from quarium_TestSuit e where e.project =:project")
                .parameter("project", params.get("project")).list();
        testSuitsDc.setItems(testSuits);
    }
}