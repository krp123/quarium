package com.company.quarium.web.screens.projectversion;

import com.company.quarium.entity.checklist.Checklist;
import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.TestCase;
import com.company.quarium.entity.project.ProjectVersion;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@UiController("quarium_ProjectVersion.edit")
@UiDescriptor("project-version-edit.xml")
@EditedEntityContainer("projectVersionDc")
@LoadDataBeforeShow
public class ProjectVersionEdit extends StandardEditor<ProjectVersion> {
    @Inject
    private CollectionLoader<TestCase> bugsDl;
    @Inject
    private Table<QaProjectRelationship> qaStatisticsTable;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private CollectionPropertyContainer<RegressChecklist> regressChecklistDc;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        bugsDl.setParameter("project", getEditedEntity());
    }

    @Subscribe
    protected void onInit(AfterShowEvent event) {
        qaStatisticsTable.addGeneratedColumn("timeTotal",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = regressChecklistDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getAssignedQa() != null)
                                        return s.getAssignedQa().equals(qa);

                                    return false;
                                })
                                .collect(Collectors.toList());
                        int totalTime = 0;
                        for (Checklist cl : qaChecklists) {
                            totalTime += cl.getHours() * 60 + cl.getMinutes();
                        }

                        int hours = totalTime / 60;
                        int minutes = totalTime % 60;
                        label.setValue(hours + "ч " + minutes + "м");
                        return label;
                    }
                });

        qaStatisticsTable.addGeneratedColumn("timeLeft",
                new Table.ColumnGenerator<QaProjectRelationship>() {
                    @Override
                    public Component generateCell(QaProjectRelationship qa) {
                        Label label = uiComponents.create(Label.NAME);
                        List<Checklist> qaChecklists = regressChecklistDc.getMutableItems().stream()
                                .filter(s -> {
                                    if (s.getAssignedQa() != null)
                                        return s.getAssignedQa().equals(qa);

                                    return false;
                                })
                                .filter(s -> {
                                    if (s.getState() != null)
                                        return !s.getState().getId().toString().equals("d9d8fd34-068d-99db-5adc-9d95731bc419");

                                    return false;
                                })
                                .collect(Collectors.toList());
                        int totalTime = 0;
                        for (Checklist cl : qaChecklists) {
                            totalTime += cl.getHours() * 60 + cl.getMinutes();
                        }

                        int hours = totalTime / 60;
                        int minutes = totalTime % 60;
                        label.setValue(hours + "ч " + minutes + "м");
                        return label;
                    }
                });
    }
}