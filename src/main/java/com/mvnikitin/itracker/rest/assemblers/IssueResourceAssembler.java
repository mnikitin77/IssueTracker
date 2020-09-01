package com.mvnikitin.itracker.rest.assemblers;

import com.mvnikitin.itracker.rest.controllers.IssueRESTController;
import com.mvnikitin.itracker.rest.controllers.ProjectRESTController;
import com.mvnikitin.itracker.rest.entities.IssueEntity;
import com.mvnikitin.itracker.rest.resources.IssueModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class IssueResourceAssembler extends
        RepresentationModelAssemblerSupport<IssueEntity, IssueModel> {

    public IssueResourceAssembler() {
        super(IssueRESTController.class,   IssueModel.class);
    }

    @Override
    public IssueModel toModel(IssueEntity entity) {

        IssueModel issueModel = instantiateModel(entity);

        issueModel.add(linkTo(
                methodOn(IssueRESTController.class)
                .one(entity.getId()))
                .withSelfRel());

        issueModel.setId(entity.getId());
        issueModel.setAssigneeId(entity.getAssigneeId());
        issueModel.setCode(entity.getCode());
        issueModel.setCreated(entity.getCreated());
        issueModel.setDescription(entity.getDescription());
        issueModel.setIssuePriorityId(entity.getIssuePriorityId());
        issueModel.setIssueStateId(entity.getIssueStateId());
        issueModel.setIssueTypeId(entity.getIssueTypeId());
        issueModel.setModified(entity.getModified());
        issueModel.setParentId(entity.getParentId());
        issueModel.setReporterId(entity.getReporterId());
        issueModel.setProjectId(entity.getProjectId());
        issueModel.setSprintId(entity.getSprintId());
        issueModel.setStoryPoints(entity.getStoryPoints());
        issueModel.setTitle(entity.getTitle());

        return issueModel;
    }

    @Override
    public CollectionModel<IssueModel> toCollectionModel(
            Iterable<? extends IssueEntity> entities) {

        CollectionModel<IssueModel> issueModels =
                super.toCollectionModel(entities);

        issueModels.add(linkTo(methodOn(IssueRESTController.class).all()).withSelfRel());

        return issueModels;
    }
}
