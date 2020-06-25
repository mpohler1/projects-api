package com.masonpohler.api.projects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderByLastModifiedDesc();
}
