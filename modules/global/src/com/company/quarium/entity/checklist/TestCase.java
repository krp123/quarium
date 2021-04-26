package com.company.quarium.entity.checklist;

import com.company.quarium.entity.references.Priority;
import com.company.quarium.entity.references.Statement;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "QUARIUM_TEST_CASE")
@Entity(name = "quarium_TestCase")
@NamePattern("%s|name")
public class TestCase extends StandardEntity {
    private static final long serialVersionUID = -2660701620585662317L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testCase", cascade = CascadeType.PERSIST)
    @OrderBy("creationDate")
    private List<Step> caseStep;

    @Column(name = "HOURS")
    @Min(message = "{msg://quarium_TestCase.hours.validation.Min}", value = 0)
    @Max(message = "{msg://quarium_TestCase.hours.validation.Max}", value = 999)
    private Integer hours;

    @Column(name = "MINUTES")
    @Min(message = "{msg://quarium_TestCase.minutes.validation.Min}", value = 0)
    @Max(message = "{msg://quarium_TestCase.minutes.validation.Max}", value = 59)
    private Integer minutes;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIORITY_ID")
    private Priority priority;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private Statement state;

    @Column(name = "STEP")
    private String step;

    @Lob
    @Column(name = "EXPECTED_RESULT")
    private String expectedResult;

    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECKLIST_ID")
    @OnDeleteInverse(DeletePolicy.CASCADE)
    private Checklist checklist;

    public List<Step> getCaseStep() {
        return caseStep;
    }

    public void setCaseStep(List<Step> caseStep) {
        this.caseStep = caseStep;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Statement getState() {
        return state;
    }

    public void setState(Statement state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

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