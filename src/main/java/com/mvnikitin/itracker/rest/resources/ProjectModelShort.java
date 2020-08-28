package com.mvnikitin.itracker.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "project")
@Relation(collectionRelation = "projects")
@JsonInclude(Include.NON_NULL)
public class ProjectModelShort extends RepresentationModel<ProjectModelShort> {

    private Long id;
    private String name;
    private String description;
    private String unit;
    private Long adminId;
    private Long ownerId;
}
