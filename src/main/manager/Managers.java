package main.manager;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}