package main.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testUtilityClassReturnsInitializedManagerInstances() {
        final HistoryManager historyManager = Managers.getDefaultHistoryManager();
        final TaskManager taskManager = Managers.getDefaultTaskManager();
        assertNotNull(historyManager, "Утилитарный класс должен возвращать инициализированный экземпляр HistoryManager.");
        assertNotNull(taskManager, "Утилитарный класс должен возвращать инициализированный экземпляр TaskManager.");
    }
}