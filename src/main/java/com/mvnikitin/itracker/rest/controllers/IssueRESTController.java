package com.mvnikitin.itracker.rest.controllers;

import com.mvnikitin.itracker.rest.assemblers.IssueResourceAssembler;
import com.mvnikitin.itracker.rest.entities.IssueEntity;
import com.mvnikitin.itracker.rest.exceptions.ResourceNotFoundException;
import com.mvnikitin.itracker.rest.repositories.IssueRepo;
import com.mvnikitin.itracker.rest.resources.IssueModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/v1/issues")
public class IssueRESTController {

    private IssueRepo repository;
    private IssueResourceAssembler assembler;

    @Autowired
    public void setRepository(IssueRepo repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAssembler(IssueResourceAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueModel> one(@PathVariable Long id) {
        return findById(id);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<IssueModel>> all() {

        List<IssueEntity> issues = repository.findAll();

        return getReponseEntitiesFromList(issues);
    }

    @GetMapping("/parent/{id}")
    public ResponseEntity<CollectionModel<IssueModel>> children(
            @PathVariable Long id) {

        List<IssueEntity> issues = repository.findByParentId(id);

        return getReponseEntitiesFromList(issues);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<CollectionModel<IssueModel>> projectIssues(
            @PathVariable Long id) {

        List<IssueEntity> issues = repository.findByProjectId(id);

        return getReponseEntitiesFromList(issues);
    }

    @GetMapping("/sprint/{id}/")
    public ResponseEntity<CollectionModel<IssueModel>> sprintIssues(
            @PathVariable Long id) {

        List<IssueEntity> issues = repository.findBySprintId(id);

        return getReponseEntitiesFromList(issues);
    }

    @Secured("ROLE_REST_MODIFY")
    @PostMapping
    public ResponseEntity<IssueModel> add(
            @RequestBody IssueEntity issueEntity) {

        if (issueEntity == null) {
            throw new IllegalArgumentException(
                    "Unable to save an Issue as null");
        }

        issueEntity.setId(0L);

        IssueModel newIssueModel =
                assembler.toModel(repository.save(issueEntity));

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .build(newIssueModel.getId()))
                .body(newIssueModel);
    }

    @Secured("ROLE_REST_MODIFY")
    @PutMapping
    public ResponseEntity<IssueModel> update(
            @RequestBody IssueEntity issueEntity) {

        if (issueEntity == null) {
            throw new IllegalArgumentException(
                    "Unable to save an Issue as null");
        }

        IssueEntity updatedIssue = repository
                .findById(issueEntity.getId())
                .map(issue -> {
                    issue.setAssigneeId(issueEntity.getAssigneeId());
                    issue.setCode(issueEntity.getCode());
                    issue.setDescription(issueEntity.getDescription());
                    issue.setCreated(issueEntity.getCreated());
                    issue.setIssuePriorityId(issueEntity.getIssuePriorityId());
                    issue.setIssueStateId(issueEntity.getIssueStateId());
                    issue.setIssueTypeId(issueEntity.getIssueTypeId());
                    issue.setModified(issueEntity.getModified());
                    issue.setParentId(issueEntity.getParentId());
                    issue.setReporterId(issueEntity.getReporterId());
                    issue.setProjectId(issueEntity.getProjectId());
                    issue.setSprintId(issueEntity.getSprintId());
                    issue.setStoryPoints(issueEntity.getStoryPoints());
                    issue.setTitle(issueEntity.getTitle());

                    return repository.save(issue);
                })
                .orElseGet(() -> repository.save(issueEntity));

        return ResponseEntity.ok(assembler.toModel(updatedIssue));
    }

    @Secured("ROLE_REST_MODIFY")
    @DeleteMapping("/{id}")
    public ResponseEntity<IssueModel> delete(@PathVariable Long id) {

        ResponseEntity<IssueModel> retval = findById(id);
        repository.deleteById(id);

        return retval;
    }

    private ResponseEntity<IssueModel> findById(Long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unable to find the issue [id = " + id + "]"));
    }

    private ResponseEntity<CollectionModel<IssueModel>>
        getReponseEntitiesFromList(List<IssueEntity> issues) {

        return new ResponseEntity<>(assembler.toCollectionModel(issues),
                HttpStatus.OK);
    }
}
