package com.mvnikitin.itracker.rest.repositories;

import com.mvnikitin.itracker.rest.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectEntity, Long> {
}
