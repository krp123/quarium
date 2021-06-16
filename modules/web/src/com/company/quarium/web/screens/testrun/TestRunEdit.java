package com.company.quarium.web.screens.testrun;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.project.TestRun;
import com.company.quarium.service.CopyChecklistService;
import com.company.quarium.web.screens.simplechecklist.TestRunTestSuitBrowse;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_TestRun.edit")
@UiDescriptor("test-run-edit.xml")
@EditedEntityContainer("testRunDc")
@LoadDataBeforeShow
public class TestRunEdit extends StandardEditor<TestRun> {

    @Inject
    private CollectionLoader<QaProjectRelationship> qaDl;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataManager dataManager;
    @Inject
    private CopyChecklistService copyChecklistService;
    @Inject
    private InstanceContainer<TestRun> testRunDc;
    @Inject
    private CollectionPropertyContainer<RegressChecklist> checklistsDc;

    @Subscribe("checklistTable.addChecklist")
    protected void onAddChecklist(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(SimpleChecklist.class, this)
                .withOptions(new MapScreenOptions(ParamsMap.of("project", getEditedEntity().getProject())))
                .withScreenClass(TestRunTestSuitBrowse.class)
                .withOpenMode(OpenMode.DIALOG)
                .withSelectHandler(checklists -> {
                    checklists.stream()
                            .forEach(this::createAndAddChecklist);
                })
                .build()
                .show();
    }

    private RegressChecklist createAndAddChecklist(Checklist checklist) {
        checklist = dataManager.load(Checklist.class).id(checklist.getId()).view("project-checklist-view").one();
        RegressChecklist checklistNew = copyChecklistService.copyRegressChecklist(checklist);
        checklistNew.setTestRun(testRunDc.getItem());
        checklistsDc.getMutableItems().add(checklistNew);
        return checklistNew;
    }

    public void setProjectParameter(Project project) {
        qaDl.setParameter("project", project);
    }
}