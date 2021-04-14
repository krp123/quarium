package com.company.quarium.entity.project;

import com.company.quarium.entity.references.Configuration;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP")
@Entity(name = "quarium_ConfigurationProjectRelationship")
public class ConfigurationProjectRelationship extends StandardEntity {
    private static final long serialVersionUID = 3383579920076369823L;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONFIGURATION_ID")
    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}