package com.company.quarium.entity.checklist;

import com.company.quarium.entity.project.Project;
import com.company.quarium.entity.project.TestRun;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "quarium_RegressChecklist")
public class RegressChecklist extends Checklist {
    private static final long serialVersionUID = -2285337094720742769L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "REGRESS_PROJECT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project regressProject;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_RUN_ID")
    private TestRun testRun;

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public Project getRegressProject() {
        return regressProject;
    }

    public void setRegressProject(Project regressProject) {
        this.regressProject = regressProject;
    }
}