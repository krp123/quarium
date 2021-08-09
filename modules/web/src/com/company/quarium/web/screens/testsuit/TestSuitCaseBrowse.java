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
}