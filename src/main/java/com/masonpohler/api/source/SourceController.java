package com.masonpohler.api.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class SourceController {

    @Autowired
    private SourceRepository repository;

    @GetMapping("/sources")
    List<Source> getAllSources() {
        return repository.findAll();
    }

    @PostMapping("/sources/create")
    Source createSource(@RequestBody Source source) {
        return repository.save(source);
    }
}
