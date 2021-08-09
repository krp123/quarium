package com.company.quarium.entity.project;

import com.company.quarium.entity.testsuit.RunTestSuit;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Table(name = "QUARIUM_TEST_RUN")
@Entity(name = "quarium_TestRun")
@NamePattern("%s|name")
public class TestRun extends StandardEntity {
    private static final long serialVersionUID = -2342653348876543152L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "RUN_START_DATE")
    private LocalDate runStartDate;

    @Column(name = "RUN_FINISH_DATE")
    private LocalDate runFinishDate;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @JoinColumn(name = "MILESTONE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Milestone milestone;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testRun")
    private List<RunTestSuit> checklists;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @JoinColumn(name = "PROJECT_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @Composition
    private Project project;

    public void setRunStartDate(LocalDate runStartDate) {
        this.runStartDate = runStartDate;
    }

    public LocalDate getRunStartDate() {
        return runStartDate;
    }

    public void setRunFinishDate(LocalDate runFinishDate) {
        this.runFinishDate = runFinishDate;
    }

    public LocalDate getRunFinishDate() {
        return runFinishDate;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public List<RunTestSuit> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<RunTestSuit> checklists) {
        this.checklists = checklists;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}