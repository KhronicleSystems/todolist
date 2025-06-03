package com.kewei.todolist;

import com.kewei.todolist.exception.ResourceNotFoundException;
import com.kewei.todolist.model.Task;
import com.kewei.todolist.repository.TaskRepository;
import com.kewei.todolist.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createNewTask_ValidTask_Success() {
        Task task = new Task("Do homework", false);
        when(taskRepository.save(ArgumentMatchers.any(Task.class))).thenReturn(task);

        Task saved = taskService.createNewTask(task);
        Assertions.assertEquals("Do homework", saved.getTask());
    }

    @Test
    void createNewTask_EmptyTask_ThrowsException() {
        Task task = new Task("", false);
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createNewTask(task));
        Assertions.assertEquals("Task name must not be empty.", thrown.getMessage());
    }

    @Test
    void getAllTasks_ReturnsTasks() {
        Task task = new Task("Task1", true);
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks();
        assertFalse(tasks.isEmpty());
        Assertions.assertEquals(1, tasks.size());
    }

    @Test
    void getAllTasks_NoTasks_ThrowsException() {
        when(taskRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.getAllTasks());
        Assertions.assertEquals("No tasks found.", thrown.getMessage());
    }

    @Test
    void findTaskById_Found() {
        Task task = new Task("Task 1", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task found = taskService.findTaskById(1L);
        Assertions.assertEquals("Task 1", found.getTask());
    }

    @Test
    void findTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.findTaskById(99L));
        Assertions.assertEquals("Task not found with ID: 99", thrown.getMessage());
    }

    @Test
    void findAllCompletedTasks_ReturnsTasks() {
        Task task = new Task("Done", true);
        when(taskRepository.findByCompletedTrue()).thenReturn(List.of(task));

        List<Task> completed = taskService.findAllCompletedTasks();
        assertFalse(completed.isEmpty());
        assertTrue(completed.getFirst().isCompleted());
    }

    @Test
    void findAllCompletedTasks_NoTasks_ThrowsException() {
        when(taskRepository.findByCompletedTrue()).thenReturn(List.of());

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.findAllCompletedTasks());
        Assertions.assertEquals("No completed tasks found.", thrown.getMessage());
    }

    @Test
    void findAllIncompleteTasks_ReturnsTasks() {
        Task task = new Task("Not done", false);
        when(taskRepository.findByCompletedFalse()).thenReturn(List.of(task));

        List<Task> incomplete = taskService.findAllIncompleteTasks();
        assertFalse(incomplete.isEmpty());
        assertFalse(incomplete.getFirst().isCompleted());
    }

    @Test
    void findAllIncompleteTasks_NoTasks_ThrowsException() {
        when(taskRepository.findByCompletedFalse()).thenReturn(List.of());

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.findAllIncompleteTasks());
        Assertions.assertEquals("No incomplete tasks found.", thrown.getMessage());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        Assertions.assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_NotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(99L));
        Assertions.assertEquals("Task not found with ID: 99", thrown.getMessage());
    }

    @Test
    void updateTask_Success() {
        Task task = new Task("Update me", true);
        task.setId(1L);

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);

        Task updated = taskService.updateTask(task);
        Assertions.assertEquals("Update me", updated.getTask());
    }

    @Test
    void updateTask_NotFound() {
        Task task = new Task("Missing task", true);
        task.setId(99L);

        when(taskRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException thrown = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(task));
        Assertions.assertEquals("Task not found with ID: 99", thrown.getMessage());
    }

    @Test
    void updateTask_EmptyTaskName() {
        Task task = new Task("", true);
        task.setId(1L);

        when(taskRepository.existsById(1L)).thenReturn(true);

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(task));
        Assertions.assertEquals("Task name must not be empty.", thrown.getMessage());
    }
}
