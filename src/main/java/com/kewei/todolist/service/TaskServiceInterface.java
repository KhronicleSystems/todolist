package com.kewei.todolist.service;

import com.kewei.todolist.model.Task;
import java.util.List;

public interface TaskServiceInterface {
    Task createNewTask(Task task);
    List<Task> getAllTasks();
    Task findTaskById(Long id);
    List<Task> findAllCompletedTasks();
    List<Task> findAllIncompleteTasks();
    void deleteTask(Long id);
    Task updateTask(Task task);
}

