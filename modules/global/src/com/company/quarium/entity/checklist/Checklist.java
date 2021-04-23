package com.company.quarium.entity.checklist;

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
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "QUARIUM_CHECKLIST")
@Entity(name = "quarium_Checklist")
@NamePattern("%s|name")
public class Checklist extends StandardEntity {
    private static final long serialVersionUID = 3978033432072095464L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "COMMENT_", length = 1000)
    private String comment;

    @Column(name = "HOURS")
    private Integer hours;

    @Column(name = "MINUTES")
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
    private QaProjectRelationship assignedQa;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_ID")
    private Statement state;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "checklist")
    private List<TestCase> testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

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