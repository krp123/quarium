package com.company.quarium.entity.checklist;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "QUARIUM_TEST_CASE")
@Entity(name = "quarium_TestCase")
@NamePattern("%s|name")
public class TestCase extends StandardEntity {
    private static final long serialVersionUID = -2660701620585662317L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = false)
    private String name;

    @Column(name = "STEP")
    private String step;

    @Lob
    @Column(name = "EXPECTED_RESULT")
    private String expectedResult;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHECKLIST_ID")
    private Checklist checklist;

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}