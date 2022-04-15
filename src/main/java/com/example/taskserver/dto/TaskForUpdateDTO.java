package com.example.taskserver.dto;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class TaskForUpdateDTO {
    private String name;
    private String description;
    @Min(value = 1)
    @Max(value = 10)
    private Long strength;
}
