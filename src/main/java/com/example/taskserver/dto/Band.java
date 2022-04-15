package com.example.taskserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import springfox.documentation.annotations.ApiIgnore;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Band {
    private Long id;
    private String bandName;

    public Band() {
    }
}
