package com.masonpohler.api.projects;

import com.masonpohler.api.source.Source;
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
    Project getProjectById(@PathVariable long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @PostMapping("/projects/create")
    Project createProject(@RequestBody Project project) {
        return repository.save(project);
    }

    @PutMapping("/project/{id}/sources/add")
    Project addSourceToProject(@RequestBody Source source, @PathVariable long id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.getSources().add(source);
        return repository.save(project);
    }

    @PutMapping("/project/{id}/sources/remove")
    Project removeSourceFromProject(@RequestBody Source source, @PathVariable long id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        project.getSources().remove(source);
        return repository.save(project);
    }

    @DeleteMapping("/projects/delete")
    void deleteProject(@RequestBody Project project) {
        repository.delete(project);
    }
}
