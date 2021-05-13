package com.company.quarium.entity.references;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "QUARIUM_DBMS")
@Entity(name = "quarium_Dbms")
@NamePattern("%s|name")
public class Dbms extends StandardEntity {
    private static final long serialVersionUID = -6985068855334582773L;

    @Column(name = "NAME")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}