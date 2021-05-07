package com.company.quarium.entity.project;

import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "quarium_ProjectVersion")
public class ProjectVersion extends Project {
    private static final long serialVersionUID = -4405963538292351831L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_OF_ID")
    private Project versionOf;

    public Project getVersionOf() {
        return versionOf;
    }

    public void setVersionOf(Project versionOf) {
        this.versionOf = versionOf;
    }
}