package com.company.quarium.entity.checklist;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "QUARIUM_STEP")
@Entity(name = "quarium_Step")
public class Step extends StandardEntity {
    private static final long serialVersionUID = -4987181767924079801L;

    @Column(name = "STEP", length = 1000)
    private String step;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_CASE_ID")
    private TestCase testCase;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}