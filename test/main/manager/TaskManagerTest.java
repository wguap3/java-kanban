package main.manager;

import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.exception.IntersectionTimeException;  // Замените на правильный путь


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager>{
    protected T taskManager;

    @BeforeEach
    abstract void setUp();
    @Test
    void testAddNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021, 1, 1, 0, 0));
        int taskId = taskManager.addTask(task);
        assertNotNull(taskManager.getIdTask(taskId), "Задача не добавлена.");
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task 1", "Description", null,TaskStatus.NEW,Duration.ofHours(3), LocalDateTime.of(2021, 1, 1, 0, 0));
        int taskId = taskManager.addTask(task);
        taskManager.removeTask(taskId);
        assertNull(taskManager.getIdTask(taskId), "Задача не удалена.");
    }

    @Test
    public void testAddTaskWithNullStartTime() {
        Task task = new Task("Task 1", "Description", null,TaskStatus.NEW,Duration.ofHours(3), LocalDateTime.of(2021, 1, 1, 0, 0));
        task.setStartTime(null);
        int taskId = taskManager.addTask(task);
        assertNotNull(taskManager.getIdTask(taskId), "Задача с null startTime не добавлена.");
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Task 1", "Description", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2000,1,1,1,1));
        int taskId = taskManager.addTask(task);
        taskManager.getIdTask(taskId);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История неверна.");
    }

    @Test
    public void testEmptyHistory(){
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    public void testDuplicationHistory(){
        Task task = new Task("Task 1", "Description", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2000,1,1,1,1));
        int taskId = taskManager.addTask(task);
        taskManager.getIdTask(taskId);
        taskManager.getIdTask(taskId);
        taskManager.getIdTask(taskId);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "Размер истории неверный,в ней есть дубликаты.");
    }

    @Test
    public void testDeleteHistoryFirstElement(){
        Task task1 = new Task("Task 1", "Description1", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2000,1,1,1,1));
        Task task2 = new Task("Task 2", "Description2", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2001,3,1,1,1));
        Task task3 = new Task("Task 3", "Description3", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2003,3,1,1,1));

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        taskManager.getIdTask(taskId1);
        taskManager.getIdTask(taskId2);
        taskManager.getIdTask(taskId3);

        taskManager.removeTask(taskId1);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Размер истории неверный после удаления задачи с начала.");
        assertEquals(taskId2,history.get(0).getId(),"Удалена не та задача.");
    }

    @Test
    public void testDeleteHistoryLastElement(){
        Task task1 = new Task("Task 1", "Description1", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2000,1,1,1,1));
        Task task2 = new Task("Task 2", "Description2", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2001,3,1,1,1));
        Task task3 = new Task("Task 3", "Description3", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2003,3,1,1,1));

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        taskManager.getIdTask(taskId1);
        taskManager.getIdTask(taskId2);
        taskManager.getIdTask(taskId3);

        taskManager.removeTask(taskId3);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Размер истории неверный после удаления задачи с конца.");
        assertEquals(taskId2,history.get(1).getId(),"Удалена не та задача.");
    }

    @Test
    public void testDeleteHistoryCentralElement(){
        Task task1 = new Task("Task 1", "Description1", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2000,1,1,1,1));
        Task task2 = new Task("Task 2", "Description2", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2001,3,1,1,1));
        Task task3 = new Task("Task 3", "Description3", null,TaskStatus.NEW,Duration.ofHours(4),LocalDateTime.of(2003,3,1,1,1));

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        taskManager.getIdTask(taskId1);
        taskManager.getIdTask(taskId2);
        taskManager.getIdTask(taskId3);

        taskManager.removeTask(taskId2);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Размер истории неверный после удаления задачи c середины.");
        assertEquals(taskId3,history.get(1).getId(),"Удалена не та задача.");
    }









}