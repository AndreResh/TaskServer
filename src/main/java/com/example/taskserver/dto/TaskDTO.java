package com.example.taskserver.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class TaskDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Min(value = 1)
    @Max(value = 10)
    private Long strength;
    @Min(1)
    private Long numberOfPeople;
}
