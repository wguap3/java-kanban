package main;

import main.manager.InMemoryTaskManager;
import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", null, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", null, TaskStatus.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", null, null);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", null, epic1.getId(), TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", null, epic1.getId(), TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", null, epic1.getId(), TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        System.out.println("Tasks:");
        System.out.println(taskManager.getAllTasks());

        System.out.println("Subtasks:");
        System.out.println(taskManager.getAllSubtask());

        System.out.println("Epics:");
        System.out.println(taskManager.getAllEpic());

        taskManager.getIdTask(1);
        taskManager.getIdTask(2);
        taskManager.getIdTask(1);

        System.out.println("История1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getIdEpic(4);
        taskManager.getIdEpic(3);
        taskManager.getIdTask(2);
        taskManager.getIdTask(1);

        System.out.println("История2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeTask(2);
        taskManager.removeSubtask(5);

        System.out.println("История3:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeEpic(3);

        System.out.println("История4:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

    }
}