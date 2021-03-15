package com.company.quarium.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Table(name = "QUARIUM_QA_PROJECT_RELATIONSHIP")
@Entity(name = "quarium_QaProjectRelationship")
public class QaProjectRelationship extends StandardEntity {
    private static final long serialVersionUID = -3773188474348230340L;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QA_ID")
    private Qa qa;

    public Qa getQa() {
        return qa;
    }

    public void setQa(Qa qa) {
        this.qa = qa;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}