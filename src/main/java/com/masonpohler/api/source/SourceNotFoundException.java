package com.masonpohler.api.source;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SourceNotFoundException extends RuntimeException {

    SourceNotFoundException(long id) {
        super("Source with id " + id + " was not found");
    }
}
