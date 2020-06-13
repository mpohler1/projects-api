package com.masonpohler.api.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @GetMapping("/projects")
    List<Project> getAllProjects() {
        return repository.findAll();
    }

    @GetMapping("/project/{id}")
    Project getProjectById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @PostMapping("/projects/create")
    Project createProject(@RequestBody Project project) {
        return repository.save(project);
    }

    @DeleteMapping("/projects/delete")
    void deleteProject(@RequestBody Project project) {
        repository.delete(project);
    }
}
