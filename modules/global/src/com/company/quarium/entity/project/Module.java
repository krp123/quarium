package com.company.quarium.entity.project;

import com.company.quarium.entity.checklist.Checklist;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "QUARIUM_MODULE")
@Entity(name = "quarium_Module")
@NamePattern("%s|name")
public class Module extends StandardEntity {
    private static final long serialVersionUID = 8234828384246143166L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "module")
    private Checklist checklist;

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}