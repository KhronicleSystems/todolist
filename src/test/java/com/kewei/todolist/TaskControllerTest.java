package com.kewei.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kewei.todolist.controller.TaskController;
import com.kewei.todolist.exception.ResourceNotFoundException;
import com.kewei.todolist.model.Task;
import com.kewei.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTask() throws Exception {
        Task task = new Task("Test JUnit", false);
        task.setId(1L);

        Mockito.when(taskService.createNewTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.task").value("Test JUnit"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task task1 = new Task("Task 1", false);
        Task task2 = new Task("Task 2", true);

        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        Task task = new Task("Task 1", false);
        task.setId(1L);

        Mockito.when(taskService.findTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.task").value("Task 1"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        Mockito.when(taskService.findTaskById(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with ID: 99"));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Task not found with ID: 99"));
    }

    @Test
    void testGetAllCompletedTasks_Success() throws Exception {
        Task task = new Task("Completed task", true);
        Mockito.when(taskService.findAllCompletedTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    @Test
    void testGetAllCompletedTasks_NotFound() throws Exception {
        Mockito.when(taskService.findAllCompletedTasks())
                .thenThrow(new ResourceNotFoundException("No completed tasks found."));

        mockMvc.perform(get("/api/v1/tasks/completed"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("No completed tasks found."));
    }

    @Test
    void testGetAllIncompleteTasks_Success() throws Exception {
        Task task = new Task("Incomplete task", false);
        Mockito.when(taskService.findAllIncompleteTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void testGetAllIncompleteTasks_NotFound() throws Exception {
        Mockito.when(taskService.findAllIncompleteTasks())
                .thenThrow(new ResourceNotFoundException("No incomplete tasks found."));

        mockMvc.perform(get("/api/v1/tasks/incomplete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("No incomplete tasks found."));
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        Task updatedTask = new Task("Updated task", true);
        updatedTask.setId(1L);

        Mockito.when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.task").value("Updated task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testUpdateTask_NotFound() throws Exception {
        Task task = new Task("No task", true);
        task.setId(99L);

        Mockito.when(taskService.updateTask(any(Task.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with ID: 99"));

        mockMvc.perform(put("/api/v1/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Task not found with ID: 99"));
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Task not found with ID: 99"))
                .when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Task not found with ID: 99"));
    }
}
