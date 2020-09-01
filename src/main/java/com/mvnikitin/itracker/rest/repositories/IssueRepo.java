package com.mvnikitin.itracker.rest.repositories;

import com.mvnikitin.itracker.rest.entities.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepo extends JpaRepository<IssueEntity, Long> {

    List<IssueEntity> findByParentId(Long parentId);
    List<IssueEntity> findByProjectId(Long projectId);
    List<IssueEntity> findBySprintId(Long sprintId);
}
