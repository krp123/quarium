package com.company.quarium.entity.references;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "QUARIUM_THESIS_VERSION")
@Entity(name = "quarium_ThesisVersion")
@NamePattern("%s|number")
public class ThesisVersion extends StandardEntity {
    private static final long serialVersionUID = -6577928712700758233L;

    @Column(name = "NUMBER_")
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}