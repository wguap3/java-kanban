package main.manager;

import main.exception.IntersectionTimeException;
import main.task.Epic;
import main.task.Task;
import main.task.Subtask;
import main.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{
    private File tempFile;

    @BeforeEach
    @Override
    void setUp() {
        try {
            tempFile = File.createTempFile("task_manager_test", ".txt");
            taskManager = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            fail("Ошибка при создании временного файла: " + e.getMessage());
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loadedM = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedM.getAllTasks().isEmpty());
        assertTrue(loadedM.getAllSubtask().isEmpty());
        assertTrue(loadedM.getAllEpic().isEmpty());
    }

    @Test
    void testSaveAndLoad() {
        Task task1 = new Task("Задача 1", "Описание 1", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика", null, TaskStatus.NEW,taskManager);
        int epic1Id = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", null, epic1Id, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2012,1,1,0,0));
        taskManager.addSubtask(subtask1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task1, loadedManager.getIdTask(1));

    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description",null, TaskStatus.NEW,Duration.ofDays(4),LocalDateTime.of(2000,1,1,1,1));
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description", null,TaskStatus.NEW,Duration.ofDays(4),LocalDateTime.of(2000,1,1,1,1));
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Неверное количество приоритетных задач.");
        assertEquals(task1, prioritizedTasks.get(0), "Задачи не отсортированы по времени.");
    }

    @Test
    public void testValidateTaskOverlap() {
        Task task1 = new Task("Task 1", "Description",null, TaskStatus.NEW,Duration.ofDays(4),LocalDateTime.of(2000,1,1,1,1));
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description", null,TaskStatus.NEW,Duration.ofDays(4),LocalDateTime.of(2000,1,1,1,1));
        task2.setStartTime(LocalDateTime.now().plusMinutes(15));
        task2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2),
                "Задачи с пересекающимися интервалами не должны добавляться.");
    }

    @Test
    public void testAddSubtask() {
        Epic epic = new Epic("Epic 1", "Description", null,TaskStatus.NEW,taskManager);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", null,epicId, TaskStatus.NEW,Duration.ofDays(3),LocalDateTime.of(2022,2,2,2,2));
        int subtaskId = taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getIdSubtask(subtaskId), "Подзадача не добавлена.");
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Epic 1", "Description",null, TaskStatus.NEW,taskManager);
        int epicId = taskManager.addEpic(epic);
        assertNotNull(taskManager.getIdEpic(epicId), "Эпик не добавлен.");
    }

    @Test
    public void testRemoveSubtask() {
        Epic epic = new Epic("Epic 1", "Description",null, TaskStatus.NEW,taskManager);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", null,epicId, TaskStatus.NEW,Duration.ofDays(3),LocalDateTime.of(2022,2,2,2,2));
        int subtaskId = taskManager.addSubtask(subtask);
        taskManager.removeSubtask(subtaskId);
        assertNull(taskManager.getIdSubtask(subtaskId), "Подзадача не удалена.");
    }

    @Test
    public void testRemoveEpic() {
        Epic epic = new Epic("Epic 1", "Description", null,TaskStatus.NEW,taskManager);
        int epicId = taskManager.addEpic(epic);
        taskManager.removeEpic(epicId);
        assertNull(taskManager.getIdEpic(epicId), "Эпик не удален.");
    }

    @Test
    public void testIntersectionTimeException() {
        Task task1 = new Task("Task 1", "Description1", null, TaskStatus.NEW, Duration.ofHours(4), LocalDateTime.of(2000, 1, 1, 1, 1));
        Task task2 = new Task("Task 2", "Description2", null, TaskStatus.NEW, Duration.ofHours(4), LocalDateTime.of(2000, 1, 1, 1, 1));

        taskManager.addTask(task1);


        assertThrows(IntersectionTimeException.class, () -> {
            taskManager.addTask(task2);
        }, "При пересечении двух задач должно выскакивать исключение.");
    }



}
