package main.manager;

import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    @Override
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }
    @Test
    void testAddAndFindTaskById() {
        Task task = new Task("Test Task", "Description of test task", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
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
    void testTaskIdConflict() {
        Task task1 = new Task("Task 1", "Description of task 1", 1, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description of task 2", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
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
        Task originalTask = new Task("Test Task", "Description of test task", 5, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
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

    @Test
    void testRemoveSubtask() {
        Epic epic = new Epic("Test Epic", "Description of test epic", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test Subtask1", "Description of test subtask1", null, epicId, TaskStatus.IN_PROGRESS, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        int subtaskId = taskManager.addSubtask(subtask1);
        taskManager.removeSubtask(subtaskId);
        Subtask foundSubtask = taskManager.getIdSubtask(subtaskId);
        assertNull(foundSubtask, "Подзадача должна быть удалена и недоступна по старому ID.");
    }

    @Test
    void testRemovedSubtaskDoesNotStoreOldId() {
        Epic epic = new Epic("Test Epic", "Description of test epic", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test Subtask1", "Description of test subtask1", null, epicId, TaskStatus.IN_PROGRESS, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        int subtaskId = taskManager.addSubtask(subtask1);
        taskManager.removeSubtask(subtaskId);
        assertNull(taskManager.getIdSubtask(subtaskId), "Подзадача должна быть удалена и недоступна по старому ID.");
        assertFalse(epic.getSubtaskIds().contains(subtaskId), "Эпик не должен хранить старый ID удаленной подзадачи.");
    }

    @Test
    void testTaskFieldChangeAffectsManager() {
        Task task = new Task("Test Task", "Description of test task", null, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.addTask(task);

        assertEquals("Test Task", task.getName(), "Название задачи должно быть 'Test Task'.");
        task.setName("Test Task Update");
        assertEquals("Test Task Update", task.getName(), "Название задачи должно быть 'Test Task Update'.");

        assertEquals("Description of test task", task.getDescribe(), "Описание задачи должно быть 'Description of test task'.");
        task.setDescribe("Description of test task Update");
        assertEquals("Description of test task Update", task.getDescribe(), "Описание задачи должно быть 'Description of test task Update'.");

        assertEquals(1, task.getId(), "Id задачи должно быть = 1.");
        task.setId(14);
        assertEquals(14, task.getId(), "Id задачи должно быть = 14.");

        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус задачи должен быть 'NEW'.");
        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus(), "Статус задачи должен быть 'DONE'.");
    }

    @Test
    void testAddSubtask(){
        Epic epic = new Epic("Epic 1", "Description",null, TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", null,epicId, TaskStatus.NEW,Duration.ofDays(4),LocalDateTime.of(2023,1,1,1,1));
        int subtaskId = taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getIdSubtask(subtaskId), "Подзадача не добавлена.");
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Epic 1", "Description", null,TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic);
        assertNotNull(taskManager.getIdEpic(epicId), "Эпик не добавлен.");
    }

    @Test
    public void testRemoveEpic() {
        Epic epic = new Epic("Epic 1", "Description", null,TaskStatus.NEW);
        int epicId = taskManager.addEpic(epic);
        taskManager.removeEpic(epicId);
        assertNull(taskManager.getIdEpic(epicId), "Эпик не удален.");
    }
}