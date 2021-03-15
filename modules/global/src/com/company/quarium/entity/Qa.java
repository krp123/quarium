package com.company.quarium.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "QUARIUM_QA")
@Entity(name = "quarium_Qa")
@NamePattern("%s|fullName")
public class Qa extends StandardEntity {
    private static final long serialVersionUID = -5396608880582597019L;

    @NotNull
    @Column(name = "FULL_NAME", nullable = false, unique = true)
    private String fullName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "qa")
    private List<QaProjectRelationship> projects;

    public List<QaProjectRelationship> getProjects() {
        return projects;
    }

    public void setProjects(List<QaProjectRelationship> projects) {
        this.projects = projects;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}