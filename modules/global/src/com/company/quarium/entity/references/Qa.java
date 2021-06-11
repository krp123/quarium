package com.company.quarium.entity.references;

import com.company.quarium.entity.project.QaProjectRelationship;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;
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

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "qa")
    private List<QaProjectRelationship> projects;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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