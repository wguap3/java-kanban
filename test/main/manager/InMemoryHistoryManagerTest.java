package main.manager;

import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testTaskHistoryPreservation() {
        //InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task originalTask = new Task("Test Task", "Description of test task", 1, TaskStatus.NEW);
        taskManager.addTask(originalTask);
        taskManager.getIdTask(1);
        Task originalTask1 = new Task("Test Task1", "Description of test task1", 1, TaskStatus.IN_PROGRESS);
        taskManager.updateTask(originalTask1);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        Task historicalTask = history.get(0);
        assertEquals(historicalTask, originalTask, "Задача в истории отличается");

    }
}