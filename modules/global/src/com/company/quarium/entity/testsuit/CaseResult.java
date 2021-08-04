package com.company.quarium.entity.testsuit;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "QUARIUM_CASE_RESULT")
@Entity(name = "quarium_CaseResult")
@NamePattern("%s %s|dateAdded,status")
public class CaseResult extends StandardEntity {
    private static final long serialVersionUID = 1368812259783565414L;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "COMMENT_", length = 1000)
    private String comment;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_CASE_ID")
    private TestCase testCase;

    @Column(name = "LINK", length = 1000)
    private String link;

    @Column(name = "DATE_ADDED")
    private LocalDateTime dateAdded;

    @Column(name = "EXECUTION_TIME")
    private LocalTime executionTime;

    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }

    public LocalTime getExecutionTime() {
        return executionTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public CaseStatus getStatus() {
        return status == null ? null : CaseStatus.fromId(status);
    }

    public void setStatus(CaseStatus status) {
        this.status = status == null ? null : status.getId();
    }
}