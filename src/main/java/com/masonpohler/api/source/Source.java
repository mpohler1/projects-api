package com.masonpohler.api.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.masonpohler.api.projects.Project;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Source {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String url;

    @ManyToMany(mappedBy = "sources", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnoreProperties("sources")
    private List<Project> projects;
}
