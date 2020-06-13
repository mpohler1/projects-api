package com.masonpohler.api.projects;

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
    private String description;
    private String detailedDescription;
    private String previewURL;
    private String liveURL;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable
    private List<Source> sources;
}
