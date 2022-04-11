package com.example.taskserver.dto;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weapon {
    private Long id;
    private Long taskId;

    public Weapon() {
    }

}
