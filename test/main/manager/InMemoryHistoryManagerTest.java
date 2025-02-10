package main.manager;

import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testTaskHistoryPreservation() {
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

    @Test
    void testLinkedListAddAndRemove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2, TaskStatus.IN_PROGRESS);
        historyManager.linkLast(task1);
        historyManager.linkLast(task2);
        assertEquals(task1, historyManager.getTasks().get(0), "Первый элемент должен быть task1.");
        assertEquals(task2, historyManager.getTasks().get(1), "Второй элемент должен быть task2.");
        assertEquals(2, historyManager.getTasks().size(), "Размер списка должен быть 2.");
        historyManager.remove(1);
        assertEquals(task2, historyManager.getTasks().get(0), "Первый элемент после удаления должен быть task2.");
        assertEquals(1, historyManager.getTasks().size(), "Размер списка после удаления должен быть 1.");
    }

}