package main.manager;

import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testTaskHistoryPreservation() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task originalTask = new Task("Test Task", "Description of test task", null, TaskStatus.NEW);

        historyManager.addTask(originalTask);
        originalTask.setDescribe("Updated description");
        originalTask.setStatus(TaskStatus.IN_PROGRESS);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        Task historicalTask = history.get(0);
        assertEquals("Description of test task", historicalTask.getDescribe(), "Описание исторической задачи должно оставаться неизменным.");
        assertEquals(TaskStatus.NEW, historicalTask.getStatus(), "Статус исторической задачи должен оставаться неизменным.");
    }
}