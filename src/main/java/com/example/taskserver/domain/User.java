package com.example.taskserver.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long userId;
    private Long bandId;
    private Long taskId;
    private String name;

    public User() {
    }
}
