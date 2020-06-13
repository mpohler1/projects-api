package com.masonpohler.api.source;

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

    @ManyToMany(mappedBy = "sources")
    private List<Project> projects;
}
