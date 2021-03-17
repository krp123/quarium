package com.company.quarium.entity.checklist;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "QUARIUM_CHECKLIST")
@Entity(name = "quarium_Checklist")
@NamePattern("%s|name")
public class Checklist extends StandardEntity {
    private static final long serialVersionUID = 3978033432072095464L;

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