package com.mvnikitin.itracker.rest.repositories;

import com.mvnikitin.itracker.rest.entities.ProjectEntityShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepoShort extends JpaRepository<ProjectEntityShort, Long> {
}
