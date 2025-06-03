package com.kewei.todolist.service;

import com.kewei.todolist.exception.ResourceNotFoundException;
import com.kewei.todolist.model.Task;
import com.kewei.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements TaskServiceInterface {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createNewTask(Task task) {
        if (task.getTask() == null || task.getTask().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name must not be empty.");
        }
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found.");
        }
        return tasks;
    }

    @Override
    public Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    @Override
    public List<Task> findAllCompletedTasks() {
        List<Task> tasks = taskRepository.findByCompletedTrue();
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No completed tasks found.");
        }
        return tasks;
    }

    @Override
    public List<Task> findAllIncompleteTasks() {
        List<Task> tasks = taskRepository.findByCompletedFalse();
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No incomplete tasks found.");
        }
        return tasks;
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getId() == null || !taskRepository.existsById(task.getId())) {
            throw new ResourceNotFoundException("Task not found with ID: " + task.getId());
        }
        if (task.getTask() == null || task.getTask().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name must not be empty.");
        }
        return taskRepository.save(task);
    }
}

