package com.masonpohler.api.projects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.masonpohler.api.source.Source;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
class Project {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Column(columnDefinition = "TINYTEXT")
    private String description;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String detailedDescription;

    private String previewURL;
    private String liveURL;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnoreProperties("projects")
    @JoinTable(
            name = "project_source",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "source_id", referencedColumnName = "id")
    )
    private Set<Source> sources = new HashSet<>();
}
