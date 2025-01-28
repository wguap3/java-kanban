package main.task;

import main.manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

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
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", epicId, epic.getId(), TaskStatus.NEW);
        Integer result = taskManager.addSubtask(subtask);
        assertNull(result, "Эпик не должен быть добавлен в самого себя в качестве подзадачи.");
    }
}