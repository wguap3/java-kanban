package main.manager;

import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddAndFindTaskById() {
        Task task = new Task("Test Task", "Description of test task", null, TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        Task foundTask = taskManager.getIdTask(taskId);
        assertNotNull(foundTask, "Задача должна быть найдена по ID.");
        assertEquals(taskId, foundTask.getId(), "ID найденной задачи должен совпадать с добавленным ID.");
        assertEquals("Test Task", foundTask.getName(), "Заголовок найденной задачи должен совпадать с добавленным заголовком.");
    }

    @Test
    void testAddAndFindEpicById() {
        Epic epic = new Epic("Test Epic", "Description of test epic", null, null);
        int epicId = taskManager.addEpic(epic);
        Epic foundEpic = taskManager.getIdEpic(epicId);
        assertNotNull(foundEpic, "Эпик должен быть найден по ID.");
        assertEquals(epicId, foundEpic.getId(), "ID найденого эпика должен совпадать с добавленным ID.");
        assertEquals("Test Epic", foundEpic.getName(), "Заголовок найденого эпика должен совпадать с добавленным заголовком.");
    }

    @Test
    void testAddAndFindSubtaskById() {
        Epic epic = new Epic("Test Epic", "Description of test epic", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Description of test subtask", null, epicId, TaskStatus.IN_PROGRESS);
        int subtaskId = taskManager.addSubtask(subtask);
        Subtask foundSubtask = taskManager.getIdSubtask(subtaskId);
        assertNotNull(foundSubtask, "Подзадача должна быть найдена по ID.");
        assertEquals(subtaskId, foundSubtask.getId(), "ID найденной подзадачи должен совпадать с добавленным ID.");
        assertEquals("Test Subtask", foundSubtask.getName(), "Заголовок найденной подзадачи должен совпадать с добавленным заголовком.");
    }

    @Test
    void testTaskIdConflict() {
        Task task1 = new Task("Task 1", "Description of task 1", 1, TaskStatus.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description of task 2", null, TaskStatus.NEW);
        taskManager.addTask(task2);
        int id3 = task2.getId();
        Task foundTask1 = taskManager.getIdTask(1);
        int task2Id = task2.getId();
        Task foundTask2 = taskManager.getIdTask(task2Id);
        assertNotNull(foundTask1, "Задача с заданным ID должна быть найдена.");
        assertNotNull(foundTask2, "Задача с сгенерированным ID должна быть найдена.");
        assertNotEquals(foundTask1.getId(), foundTask2.getId(), "ID задач не должны совпадать.");
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task originalTask = new Task("Test Task", "Description of test task", 5, TaskStatus.NEW);
        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescribe();
        TaskStatus originalStatus = originalTask.getStatus();
        taskManager.addTask(originalTask);
        Task foundTask = taskManager.getIdTask(originalTask.getId()); // Получаем задачу по ID
        assertNotNull(foundTask, "Задача должна быть найдена.");
        assertEquals(originalName, foundTask.getName(), "Имя задачи должно оставаться неизменным.");
        assertEquals(originalDescription, foundTask.getDescribe(), "Описание задачи должно оставаться неизменным.");
        assertEquals(originalStatus, foundTask.getStatus(), "Статус задачи должен оставаться неизменным.");
    }
}