package main.task;

import main.manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    void addNewSubtask() {
        Epic epic1 = new Epic("Test addNewSubtask epic", "Test addNewSubtask description epic", null, null);
        taskManager.addEpic(epic1);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", null, epic1.getId(), TaskStatus.IN_PROGRESS);
        final int id = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getIdSubtask(id);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        final List<Subtask> subtasks = taskManager.getAllSubtask();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество родзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addSubtasksInEpic() {
        Epic epic1 = new Epic("Test addSubtasksInEpic epic", "Test addSubtasksInEpic description epic", null, null);
        taskManager.addEpic(epic1);
        Subtask subtask = new Subtask("Test addSubtasksInEpic", "Test addSubtasksInEpic description", epic1.getId(), epic1.getId(), TaskStatus.IN_PROGRESS);
        Integer result = taskManager.addSubtask(subtask);
        assertNull(result, "Объект Subtask нельзя сделать своим же эпиком.");
    }
}