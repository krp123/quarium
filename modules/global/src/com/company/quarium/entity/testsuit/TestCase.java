package com.company.quarium.entity.testsuit;

import com.company.quarium.entity.references.Priority;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static com.company.quarium.Constants.PRIORITY_MEDIUM;

@Table(name = "QUARIUM_TEST_CASE")
@Entity(name = "quarium_TestCase")
@NamePattern("%s|name")
public class TestCase extends StandardEntity implements Cloneable {
    private static final long serialVersionUID = -2660701620585662317L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "INITIAL_CONDITIONS", length = 1000)
    private String initialConditions;

    @Column(name = "NUMBER_")
    private Integer number;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testCase", cascade = CascadeType.PERSIST)
    @OrderBy("number")
    private List<Step> caseStep;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testCase")
    private List<CaseResult> result;

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

    @Column(name = "STATUS_")
    private String status;

    @Lob
    @Column(name = "EXPECTED_RESULT")
    private String expectedResult;

    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECKLIST_ID")
    @OnDeleteInverse(DeletePolicy.CASCADE)
    private TestSuit testSuit;

    public List<CaseResult> getResult() {
        return result;
    }

    public void setResult(List<CaseResult> result) {
        this.result = result;
    }

    public CaseStatus getStatus() {
        return status == null ? null : CaseStatus.fromId(status);
    }

    public void setStatus(CaseStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public String getInitialConditions() {
        return initialConditions;
    }

    public void setInitialConditions(String initialConditions) {
        this.initialConditions = initialConditions;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @PostConstruct
    private void initPriority(DataManager dataManager) {
        setPriority(dataManager.load(Priority.class).id(PRIORITY_MEDIUM).one());
    }

    public TestSuit getTestSuit() {
        return testSuit;
    }

    public void setTestSuit(TestSuit testSuit) {
        this.testSuit = testSuit;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}