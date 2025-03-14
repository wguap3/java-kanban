package main.task;

import main.manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", null, null);
        final int id = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getIdEpic(id);
        assertNotNull(savedEpic, "Эпик не найдет");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
        final List<Epic> epics = taskManager.getAllEpic();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void epicAddEpic() {
        Epic epic = new Epic("Test epicAddEpic", "Test epicAddEpic description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", epicId, epic.getId(), TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        Integer result = taskManager.addSubtask(subtask);
        assertNull(result, "Эпик не должен быть добавлен в самого себя в качестве подзадачи.");
    }

    @Test
    void allStatusNew(){
        Epic epic = new Epic("Test allStatusNew", "Test allStatusNew description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test allStatusNew1", "Test  description1", null, epicId, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        Subtask subtask2 = new Subtask("Test allStatusNew2", "Test  description2", null, epicId, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW,epic.getStatus(),"Неверный статус эпика,ожидалось NEW");
    }

    @Test
    void allStatusDone(){
        Epic epic = new Epic("Test allStatusDone", "Test allStatusDone description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test allStatusDone1", "Test  description1", null, epicId, TaskStatus.DONE, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        Subtask subtask2 = new Subtask("Test allStatusDone2", "Test  description2", null, epicId, TaskStatus.DONE, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.DONE,epic.getStatus(),"Неверный статус эпика,ожидалось DONE");
    }

    @Test
    void StatusNewAndDone(){
        Epic epic = new Epic("Test StatusNewAndDone", "Test StatusNewAndDone description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test StatusNewAndDone1", "Test  description1", null, epicId, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2023,1,1,0,0));
        Subtask subtask2 = new Subtask("Test StatusNewAndDone2", "Test  description2", null, epicId, TaskStatus.DONE, Duration.ofHours(3), LocalDateTime.of(2023,1,1,0,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus(),"Неверный статус эпика,ожидалось NEW");
    }

    @Test
    void allStatusInProgress(){
        Epic epic = new Epic("Test allStatusInProgress", "Test allStatusInProgress description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test allStatusInProgress1", "Test  description1", null, epicId, TaskStatus.IN_PROGRESS, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        Subtask subtask2 = new Subtask("Test allStatusInProgress2", "Test  description2", null, epicId, TaskStatus.IN_PROGRESS, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus(),"Неверный статус эпика,ожидалось DONE");
    }

    @Test
    void StatusInProgressAndDone(){
        Epic epic = new Epic("Test StatusInProgressAndDone", "Test StatusInProgressAndDone description", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test StatusInProgressAndDone1", "Test  description1", null, epicId, TaskStatus.IN_PROGRESS, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        Subtask subtask2 = new Subtask("Test StatusInProgressAndDone2", "Test  description2", null, epicId, TaskStatus.DONE, Duration.ofHours(3), LocalDateTime.of(2022,1,1,0,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus(),"Неверный статус эпика,ожидалось DONE");
    }
}