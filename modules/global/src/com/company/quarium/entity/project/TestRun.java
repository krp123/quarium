package com.company.quarium.entity.project;

import com.company.quarium.entity.checklist.RegressChecklist;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

@Table(name = "QUARIUM_TEST_RUN")
@Entity(name = "quarium_TestRun")
@NamePattern("%s|name")
public class TestRun extends StandardEntity {
    private static final long serialVersionUID = -2342653348876543152L;

    @Column(name = "NAME")
    private String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testRun")
    private List<RegressChecklist> checklists;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public List<RegressChecklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<RegressChecklist> checklists) {
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