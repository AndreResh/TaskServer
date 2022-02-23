package com.example.taskserver.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weapon {
    private Long id;
    private Integer damage;
    private Long task_id;
    private Long band_id;

    public Weapon() {
    }

}
