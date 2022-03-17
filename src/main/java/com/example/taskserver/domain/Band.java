package com.example.taskserver.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Band {
    private Long id;
    private String bandName;

    public Band() {
    }
}
