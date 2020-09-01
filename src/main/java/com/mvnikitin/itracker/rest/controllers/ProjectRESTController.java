package com.mvnikitin.itracker.rest.controllers;

import com.mvnikitin.itracker.rest.assemblers.ProjectResourceAssembler;
import com.mvnikitin.itracker.rest.assemblers.ProjectResourceAssemblerShort;
import com.mvnikitin.itracker.rest.entities.ProjectEntityShort;
import com.mvnikitin.itracker.rest.entities.ProjectEntity;
import com.mvnikitin.itracker.rest.exceptions.ResourceNotFoundException;
import com.mvnikitin.itracker.rest.repositories.ProjectRepoShort;
import com.mvnikitin.itracker.rest.repositories.ProjectRepo;
import com.mvnikitin.itracker.rest.resources.ProjectModelShort;
import com.mvnikitin.itracker.rest.resources.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/v1/projects")
public class ProjectRESTController {

    private ProjectRepo repositoryFull;
    private ProjectRepoShort repository;
    private ProjectResourceAssembler assemblerFull;
    private ProjectResourceAssemblerShort assembler;

    @Autowired
    public void setRepositoryFull(ProjectRepo repositoryFull) {
        this.repositoryFull = repositoryFull;
    }

    @Autowired
    public void setRepository(ProjectRepoShort repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAssemblerFull(ProjectResourceAssembler assemblerFull) {
        this.assemblerFull = assemblerFull;
    }

    @Autowired
    public void setAssembler(ProjectResourceAssemblerShort assembler) {
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectModel> one(@PathVariable Long id) {
        return findById(id);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ProjectModel>> all() {

        List<ProjectEntity> projects = repositoryFull.findAll();

        return new ResponseEntity<>(assemblerFull.toCollectionModel(projects),
                HttpStatus.OK);
    }

    @Secured("ROLE_REST_MODIFY")
    @PostMapping
    public ResponseEntity<ProjectModelShort> add(
            @RequestBody ProjectEntityShort projectEntity) {

        if (projectEntity == null) {
            throw new IllegalArgumentException(
                    "Unable to save a Project as null");
        }

        projectEntity.setId(0L);

        ProjectModelShort newProjectModel =
                assembler.toModel(repository.save(projectEntity));

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .build(newProjectModel.getId()))
                .body(newProjectModel);
    }

    @Secured("ROLE_REST_MODIFY")
    @PutMapping
    public ResponseEntity<ProjectModelShort> update(
            @RequestBody ProjectEntityShort projectEntity) {

        if (projectEntity == null) {
            throw new IllegalArgumentException(
                    "Unable to save a Project as null");
        }

        ProjectEntityShort updatedProject = repository
                .findById(projectEntity.getId())
                .map(project -> {
                    project.setAdminId(projectEntity.getAdminId());
                    project.setOwnerId(projectEntity.getOwnerId());
                    project.setDescription(projectEntity.getDescription());
                    project.setName(projectEntity.getName());
                    project.setUnit(projectEntity.getUnit());

                    return repository.save(project);
                })
                .orElseGet(() -> repository.save(projectEntity));

        return ResponseEntity.ok(assembler.toModel(updatedProject));
    }

    @Secured("ROLE_REST_MODIFY")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectModel> delete(@PathVariable Long id) {

        ResponseEntity<ProjectModel> retval = findById(id);
        repositoryFull.deleteById(id);

        return retval;
    }

    private ResponseEntity<ProjectModel> findById(Long id) {
        return repositoryFull.findById(id)
                .map(assemblerFull::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unable to find the project [id = " + id + "]"));
    }
}
