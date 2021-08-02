package com.company.quarium.entity.project;

import com.company.quarium.entity.references.Dbms;
import com.company.quarium.entity.references.ThesisVersion;
import com.company.quarium.entity.testSuit.RunTestSuit;
import com.company.quarium.entity.testSuit.SharedTestSuit;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "QUARIUM_PROJECT", indexes = {
        @Index(name = "IDX_QUARIUM_PROJECT", columnList = "PROJECT_NAME, DESCRIPTION")
})
@Entity(name = "quarium_Project")
@NamePattern("%s|projectName")
public class Project extends StandardEntity {
    private static final long serialVersionUID = 8986128378201827146L;

    @Column(name = "PROJECT_NAME")
    private String projectName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<Milestone> milestone;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    private List<TestRun> testRun;

    @Temporal(TemporalType.DATE)
    @Column(name = "REGRESS_START_DATE")
    private Date regressStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "REGRESS_FINISH_DATE")
    private Date regressFinishDate;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DBMS_ID")
    private Dbms dbms;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THESIS_VERSION_ID")
    private ThesisVersion thesisVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "regressProject", cascade = CascadeType.PERSIST)
    private List<RunTestSuit> runTestSuit;

    @OneToMany(mappedBy = "project")
    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    private List<Module> module;

    @Composition
    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @OnDelete(DeletePolicy.CASCADE)
    private List<SharedTestSuit> testSuit;

    @Column(name = "CURRENT_RELEASE")
    private String currentRelease;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    private List<QaProjectRelationship> qa;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    private List<ConfigurationProjectRelationship> configuration;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    public List<TestRun> getTestRun() {
        return testRun;
    }

    public void setTestRun(List<TestRun> testRun) {
        this.testRun = testRun;
    }

    public List<Milestone> getMilestone() {
        return milestone;
    }

    public void setMilestone(List<Milestone> milestone) {
        this.milestone = milestone;
    }

    public ThesisVersion getThesisVersion() {
        return thesisVersion;
    }

    public void setThesisVersion(ThesisVersion thesisVersion) {
        this.thesisVersion = thesisVersion;
    }

    public Dbms getDbms() {
        return dbms;
    }

    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    public Date getRegressFinishDate() {
        return regressFinishDate;
    }

    public void setRegressFinishDate(Date regressFinishDate) {
        this.regressFinishDate = regressFinishDate;
    }

    public Date getRegressStartDate() {
        return regressStartDate;
    }

    public void setRegressStartDate(Date regressStartDate) {
        this.regressStartDate = regressStartDate;
    }

    public Project() {
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<RunTestSuit> getRunTestSuit() {
        return runTestSuit;
    }

    public void setRunTestSuit(List<RunTestSuit> runTestSuit) {
        this.runTestSuit = runTestSuit;
    }

    public String getCurrentRelease() {
        return currentRelease;
    }

    public void setCurrentRelease(String currentRelease) {
        this.currentRelease = currentRelease;
    }

    public List<Module> getModule() {
        return module;
    }

    public void setModule(List<Module> module) {
        this.module = module;
    }

    public List<ConfigurationProjectRelationship> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(List<ConfigurationProjectRelationship> configuration) {
        this.configuration = configuration;
    }

    public List<SharedTestSuit> getTestSuit() {
        return testSuit;
    }

    public void setTestSuit(List<SharedTestSuit> testSuit) {
        this.testSuit = testSuit;
    }

    public List<QaProjectRelationship> getQa() {
        return qa;
    }

    public void setQa(List<QaProjectRelationship> qa) {
        this.qa = qa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}