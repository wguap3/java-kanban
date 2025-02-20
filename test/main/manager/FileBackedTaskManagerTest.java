package main.manager;

import main.task.Epic;
import main.task.Task;
import main.task.Subtask;
import main.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File tempFile;

    // Создание временного файла
    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager_test", ".txt");
        taskManager = new FileBackedTaskManager(tempFile);
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
        Task task1 = new Task("Задача 1", "Описание 1", null, TaskStatus.NEW);
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика", null, TaskStatus.NEW);
        int epic1Id = taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", null, epic1Id, TaskStatus.NEW);
        taskManager.addSubtask(subtask1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task1, loadedManager.getIdTask(1));

    }
}
