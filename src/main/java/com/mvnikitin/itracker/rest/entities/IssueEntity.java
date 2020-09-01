package com.mvnikitin.itracker.rest.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "issues")
public class IssueEntity {

    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_type_id")
    private Long issueTypeId;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "issue_state_id")
    private Long issueStateId;
    @Column(name = "issue_priority_id")
    private Long issuePriorityId;
    @Column(name = "sprint_id")
    private Long sprintId;
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "assignee_id")
    private Long assigneeId;
    @Column(name = "reporter_id")
    private Long reporterId;
    @Column
    private String code;
    @Column(name = "story_points")
    private Integer storyPoints;
    @Column
    private String title;
    @Column
    private String description;
    @Column(insertable = false, updatable = false)
    LocalDateTime created;
    @Column(insertable = false)
    LocalDateTime modified;
}
