package main.manager;

import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testTaskHistoryPreservation() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task originalTask = new Task("Test Task", "Description of test task", 1, TaskStatus.NEW, Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.addTask(originalTask);
        taskManager.getIdTask(1);
        Task originalTask1 = new Task("Test Task1", "Description of test task1", 1, TaskStatus.IN_PROGRESS,Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        taskManager.updateTask(originalTask1);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        Task historicalTask = history.get(0);
        assertEquals(historicalTask, originalTask, "Задача в истории отличается");

    }

    @Test
    void testLinkedListAddAndRemove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW,Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2, TaskStatus.IN_PROGRESS,Duration.ofHours(3), LocalDateTime.of(2021,1,1,0,0));
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        assertEquals(task1, historyManager.getHistory().get(0), "Первый элемент должен быть task1.");
        assertEquals(task2, historyManager.getHistory().get(1), "Второй элемент должен быть task2.");
        assertEquals(2, historyManager.getHistory().size(), "Размер списка должен быть 2.");
        historyManager.remove(1);
        assertEquals(task2, historyManager.getHistory().get(0), "Первый элемент после удаления должен быть task2.");
        assertEquals(1, historyManager.getHistory().size(), "Размер списка после удаления должен быть 1.");
    }

}