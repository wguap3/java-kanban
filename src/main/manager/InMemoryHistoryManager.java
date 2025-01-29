package main.manager;

import main.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE = 11;
    private final List<Task> historyStorage = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (historyStorage.size() >= MAX_SIZE) {
            historyStorage.remove(0);
        }
        historyStorage.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyStorage;
    }
}