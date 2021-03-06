package com.company.quarium.entity.testsuit;

import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.references.Statement;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static com.company.quarium.Constants.STATE_NOT_STARTED;

@Table(name = "QUARIUM_TESTSUIT")
@Entity(name = "quarium_TestSuit")
@NamePattern("%s|name")
public class TestSuit extends StandardEntity {
    private static final long serialVersionUID = 3978033432072095464L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "INITIAL_CONDITIONS", length = 1000)
    private String initialConditions;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CARD_ID")
    private TestSuit parentCard;

    @Column(name = "COMMENT_", length = 1000)
    private String comment;

    @Column(name = "HOURS")
    @Min(message = "{msg://quarium_Checklist.hours.validation.Min}", value = 0)
    @Max(message = "{msg://quarium_Checklist.hours.validation.Max}", value = 999)
    @Digits(message = "{msg://quarium_Checklist.hours.validation.Digits}", integer = 3, fraction = 0)
    private Integer hours;

    @Column(name = "MINUTES")
    @Min(message = "{msg://quarium_Checklist.minutes.validation.Min}", value = 0)
    @Max(message = "{msg://quarium_Checklist.minutes.validation.Max}", value = 59)
    @Digits(message = "{msg://quarium_Checklist.minutes.validation.Digits}", integer = 3, fraction = 0)
    private Integer minutes;

    @Column(name = "TICKET", length = 1000)
    private String ticket;

    @JoinColumn(name = "MODULE_ID")
    @OnDelete(DeletePolicy.UNLINK)
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    private Module module;

    @Column(name = "IS_USED_IN_REGRESS")
    private Boolean isUsedInRegress;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @JoinColumn(name = "ASSIGNED_QA_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @Lookup(type = LookupType.DROPDOWN, actions = {})
    private QaProjectRelationship assignedQa;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private Statement state;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testSuit", cascade = CascadeType.PERSIST)
    @OrderBy("number")
    private List<TestCase> testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public String getInitialConditions() {
        return initialConditions;
    }

    public void setInitialConditions(String initialConditions) {
        this.initialConditions = initialConditions;
    }

    public TestSuit getParentCard() {
        return parentCard;
    }

    public void setParentCard(TestSuit parentCard) {
        this.parentCard = parentCard;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Boolean getIsUsedInRegress() {
        return isUsedInRegress;
    }

    public void setIsUsedInRegress(Boolean isUsedInRegress) {
        this.isUsedInRegress = isUsedInRegress;
    }

    public void setAssignedQa(QaProjectRelationship assignedQa) {
        this.assignedQa = assignedQa;
    }

    public QaProjectRelationship getAssignedQa() {
        return assignedQa;
    }

    public Statement getState() {
        return state;
    }

    public void setState(Statement state) {
        this.state = state;
    }

    @PostConstruct
    private void initState(DataManager dataManager) {
        setState(dataManager.load(Statement.class).id(STATE_NOT_STARTED).one());
        setMinutes(0);
        setHours(0);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<TestCase> getTestCase() {
        return testCase;
    }

    public void setTestCase(List<TestCase> testCase) {
        this.testCase = testCase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}