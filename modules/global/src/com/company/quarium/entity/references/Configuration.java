package com.company.quarium.entity.references;

import com.company.quarium.entity.project.ConfigurationProjectRelationship;
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

@Table(name = "QUARIUM_CONFIGURATION")
@Entity(name = "quarium_Configuration")
@NamePattern("%s|configuration")
public class Configuration extends StandardEntity {
    private static final long serialVersionUID = 992487364444438654L;

    @NotNull
    @Column(name = "CONFIGURATION", nullable = false)
    private String configuration;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "configuration")
    private List<ConfigurationProjectRelationship> project;

    public List<ConfigurationProjectRelationship> getProject() {
        return project;
    }

    public void setProject(List<ConfigurationProjectRelationship> project) {
        this.project = project;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}