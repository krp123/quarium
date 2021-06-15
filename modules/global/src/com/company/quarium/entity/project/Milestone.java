package com.company.quarium.entity.project;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "QUARIUM_MILESTONE")
@Entity(name = "quarium_Milestone")
@NamePattern("%s|name")
public class Milestone extends StandardEntity {
    private static final long serialVersionUID = -7933190383519705666L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "FINISH_DATE")
    private LocalDateTime finishDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

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

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
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