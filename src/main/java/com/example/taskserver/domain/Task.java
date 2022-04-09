package com.example.taskserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Table(value = "task")
public class Task {
    @Id
    @PrimaryKeyColumn(
            name = "id",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private Long id;
    @Column(value = "name")
    @NotBlank
    @NotNull
    private String name;
    @Column(value = "description")
    @NotBlank
    @NotNull
    private String description;
    @Column(value = "completed")
    private boolean isCompleted;
    @Column(value = "band_id")
    private Long bandId;
    @Column(value = "strength")
    @Min(value = 1)
    @Max(value = 10)
    private Long strength;
}
