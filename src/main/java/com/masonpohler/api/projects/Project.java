package com.masonpohler.api.projects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.masonpohler.api.source.Source;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Project {

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
    @JoinTable
    @JsonIgnoreProperties("projects")
    private List<Source> sources;
}
