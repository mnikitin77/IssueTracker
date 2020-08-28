package com.mvnikitin.itracker.rest.assemblers;

import com.mvnikitin.itracker.rest.controllers.ProjectRESTController;
import com.mvnikitin.itracker.rest.entities.ProjectEntity;
import com.mvnikitin.itracker.rest.resources.ProjectModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProjectResourceAssembler extends
        RepresentationModelAssemblerSupport<ProjectEntity, ProjectModel> {

    public ProjectResourceAssembler() {
        super(ProjectRESTController.class,   ProjectModel.class);
    }

    @Override
    public ProjectModel toModel(ProjectEntity entity) {

        ProjectModel projectModel = instantiateModel(entity);

        projectModel.add(linkTo(
                methodOn(ProjectRESTController.class)
                .one(entity.getId()))
                .withSelfRel());

        projectModel.setId(entity.getId());
        projectModel.setName(entity.getName());
        projectModel.setDescription(entity.getDescription());
        projectModel.setUnit(entity.getUnit());
        projectModel.setAdmin(entity.getAdmin());
        projectModel.setOwner(entity.getOwner());

        return projectModel;
    }

    @Override
    public CollectionModel<ProjectModel> toCollectionModel(
            Iterable<? extends ProjectEntity> entities) {

        CollectionModel<ProjectModel> projectModels =
                super.toCollectionModel(entities);

        projectModels.add(linkTo(methodOn(ProjectRESTController.class).all()).withSelfRel());

        return projectModels;
    }
}
