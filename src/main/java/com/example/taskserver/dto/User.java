package com.example.taskserver.dto;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long userId;
    private Long taskId;

    public User() {
    }
}
