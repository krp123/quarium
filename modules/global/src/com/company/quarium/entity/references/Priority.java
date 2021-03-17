package com.company.quarium.entity.references;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "QUARIUM_PRIORITY")
@Entity(name = "quarium_Priority")
@NamePattern("%s|name")
public class Priority extends StandardEntity {
    private static final long serialVersionUID = 2060570457621885151L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}