package com.masonpohler.api.source;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Source {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String url;
}
