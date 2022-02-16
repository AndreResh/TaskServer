package com.example.taskserver.repository;



import com.example.taskserver.domain.Task;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends CassandraRepository<Task,Long> {
    @Query("select * from task where name=:name allow filtering")
    public Task findByName(@Param("name") String name);
    @Query("select * from task")
    public List<Task> findAllTasks();
    @Query("select * from task where id=:id allow filtering")
    public Task getTaskById(@Param("id")Long id);
    @Query("update task " +
            "set name=:name, description=:description,  " +
            "strength=:strength, number_of_people=:numberOfPeople where id=:id")
    public Task update(@Param("id")Long id, @Param("name")String name, @Param("description")String description,
                       @Param("strength")Long strength,  @Param("numberOfPeople")Long numberOfPeople);
}
