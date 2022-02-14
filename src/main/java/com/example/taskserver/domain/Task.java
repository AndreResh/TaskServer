package com.example.taskserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;


@Data
@Table("task")
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @PrimaryKeyColumn(
            name = "id",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private Long id;
    @Column(value = "name")
    private String name;
    @Column(value = "description")
    private String description;
    @Column(value = "completed")
    private boolean isCompleted;
    @Column(value = "band_id")
    private Long bandId;
    @Column(value = "user_id")
    private Long userId;
    @Column(value = "weapon_id")
    private Long weaponId;
}
