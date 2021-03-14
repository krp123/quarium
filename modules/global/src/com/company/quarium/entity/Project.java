package com.company.quarium.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "QUARIUM_PROJECT")
@Entity(name = "quarium_Project")
@NamePattern("%s|description")
public class Project extends StandardEntity {
    private static final long serialVersionUID = 8986128378201827146L;

    @Column(name = "PROJECT_NAME", nullable = false, unique = true)
    @NotNull
    private String projectName;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

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