package com.company.quarium.web.screens.simplechecklist;

import com.company.quarium.entity.checklist.SimpleChecklist;
import com.company.quarium.entity.project.Project;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("quarium_TestRunTestSuitBrowse.browse")
@UiDescriptor("testRun-testSuit-browse.xml")
@LookupComponent("testRunTestSuitTable")
@LoadDataBeforeShow
public class TestRunTestSuitBrowse extends StandardLookup<SimpleChecklist> {
    @Inject
    private CollectionLoader<SimpleChecklist> simpleChecklistsDl;

    @Subscribe
    public void onInit(InitEvent event) {
        ScreenOptions options = event.getOptions();
        if (options instanceof MapScreenOptions) {
            Project project = (Project) ((MapScreenOptions) options).getParams().get("project");
            simpleChecklistsDl.setParameter("project", project);
        }
    }
}