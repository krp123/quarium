package com.company.quarium.entity.project;

import com.company.quarium.entity.checklist.RegressChecklist;
import com.company.quarium.entity.checklist.SimpleChecklist;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "QUARIUM_PROJECT", indexes = {
        @Index(name = "IDX_QUARIUM_PROJECT", columnList = "PROJECT_NAME, DESCRIPTION")
})
@Entity(name = "quarium_Project")
@NamePattern("%s|projectName")
public class Project extends StandardEntity {
    private static final long serialVersionUID = 8986128378201827146L;

    @Column(name = "PROJECT_NAME", nullable = false)
    @NotNull
    private String projectName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "regressProject", cascade = CascadeType.PERSIST)
    private List<RegressChecklist> regressChecklist;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "versionOf")
    private List<ProjectVersion> projectVersion;

    @OneToMany(mappedBy = "project")
    @OnDelete(DeletePolicy.CASCADE)
    @Composition
    private List<Module> module;

    @Composition
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private List<SimpleChecklist> checklist;

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

    public List<ProjectVersion> getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(List<ProjectVersion> projectVersion) {
        this.projectVersion = projectVersion;
    }

    public List<RegressChecklist> getRegressChecklist() {
        return regressChecklist;
    }

    public void setRegressChecklist(List<RegressChecklist> regressChecklist) {
        this.regressChecklist = regressChecklist;
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

    public List<SimpleChecklist> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<SimpleChecklist> checklist) {
        this.checklist = checklist;
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