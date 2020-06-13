package com.masonpohler.api.projects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends RuntimeException {

    ProjectNotFoundException(long id) {
        super("Project with id " + id + " not found");
    }
}
