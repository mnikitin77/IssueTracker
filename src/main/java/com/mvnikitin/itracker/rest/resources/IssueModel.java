package com.mvnikitin.itracker.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Column;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "issue")
@Relation(collectionRelation = "issues")
@JsonInclude(Include.NON_NULL)
public class IssueModel extends RepresentationModel<IssueModel> {

    private Long id;
    private Long issueTypeId;
    private Long projectId;
    private Long issueStateId;
    private Long issuePriorityId;
    private Long sprintId;
    private Long parentId;
    private Long assigneeId;
    private Long reporterId;
    private String code;
    private Integer storyPoints;
    private String title;
    private String description;
    LocalDateTime created;
    LocalDateTime modified;
}
