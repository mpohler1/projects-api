package com.masonpohler.api.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @GetMapping("/projects")
    List<Project> getAllProjects() {
        return repository.findAll();
    }
}
