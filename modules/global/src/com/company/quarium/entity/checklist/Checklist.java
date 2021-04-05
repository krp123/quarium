package com.company.quarium.entity.checklist;

import com.company.quarium.entity.project.Project;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "QUARIUM_CHECKLIST")
@Entity(name = "quarium_Checklist")
@NamePattern("%s|name")
public class Checklist extends StandardEntity {
    private static final long serialVersionUID = 3978033432072095464L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "checklist")
    private List<TestCase> testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<TestCase> getTestCase() {
        return testCase;
    }

    public void setTestCase(List<TestCase> testCase) {
        this.testCase = testCase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}