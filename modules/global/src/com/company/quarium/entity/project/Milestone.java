package com.company.quarium.entity.project;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "QUARIUM_MILESTONE")
@Entity(name = "quarium_Milestone")
@NamePattern("%s|name")
public class Milestone extends StandardEntity {
    private static final long serialVersionUID = -7933190383519705666L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "FINISH_DATE")
    private LocalDate finishDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public MilestoneStatus getStatus() {
        return status == null ? null : MilestoneStatus.fromId(status);
    }

    public void setStatus(MilestoneStatus status) {
        this.status = status == null ? null : status.getId();
    }

    @PostConstruct
    public void initStatus() {
        setStatus(MilestoneStatus.ACTIVE);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}