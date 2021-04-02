package com.company.quarium.entity.project;

import com.company.quarium.entity.checklist.Checklist;
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

    @Column(name = "PROJECT_NAME", nullable = false, unique = true)
    @NotNull
    private String projectName;

    @Composition
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    private List<Checklist> checklist;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "project")
    private List<QaProjectRelationship> qa;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    public List<Checklist> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<Checklist> checklist) {
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