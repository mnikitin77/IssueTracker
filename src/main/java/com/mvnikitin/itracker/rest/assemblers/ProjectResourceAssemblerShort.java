package com.mvnikitin.itracker.rest.assemblers;

import com.mvnikitin.itracker.rest.controllers.ProjectRESTController;
import com.mvnikitin.itracker.rest.entities.ProjectEntityShort;
import com.mvnikitin.itracker.rest.resources.ProjectModelShort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProjectResourceAssemblerShort extends
        RepresentationModelAssemblerSupport<ProjectEntityShort, ProjectModelShort> {

    public ProjectResourceAssemblerShort() {
        super(ProjectRESTController.class,   ProjectModelShort.class);
    }

    @Override
    public ProjectModelShort toModel(ProjectEntityShort entity) {

        ProjectModelShort projectModel = instantiateModel(entity);

        projectModel.add(linkTo(
                methodOn(ProjectRESTController.class)
                .one(entity.getId()))
                .withSelfRel());

        projectModel.setId(entity.getId());
        projectModel.setName(entity.getName());
        projectModel.setDescription(entity.getDescription());
        projectModel.setUnit(entity.getUnit());
        projectModel.setAdminId(entity.getAdminId());
        projectModel.setOwnerId(entity.getOwnerId());

        return projectModel;
    }

    @Override
    public CollectionModel<ProjectModelShort> toCollectionModel(
            Iterable<? extends ProjectEntityShort> entities) {

        CollectionModel<ProjectModelShort> projectModels =
                super.toCollectionModel(entities);

        projectModels.add(linkTo(methodOn(ProjectRESTController.class).all()).withSelfRel());

        return projectModels;
    }
}
