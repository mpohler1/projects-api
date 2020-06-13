package com.masonpohler.api.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class SourceController {

    @Autowired
    private SourceRepository repository;

    @GetMapping("/sources")
    List<Source> getAllSources() {
        return repository.findAll();
    }

    @GetMapping("/source/{id}")
    Source getSourceById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SourceNotFoundException(id));
    }

    @PostMapping("/sources/create")
    Source createSource(@RequestBody Source source) {
        return repository.save(source);
    }

    @DeleteMapping("sources/delete")
    void deleteSource(@RequestBody Source source) {
        repository.delete(source);
    }
}
