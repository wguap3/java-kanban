package main.task;

import main.manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        final int id = taskManager.addTask(task);
        final Task savedTask = taskManager.getIdTask(id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}