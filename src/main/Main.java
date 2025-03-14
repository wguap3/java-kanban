package main;

import main.manager.FileBackedTaskManager;
import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Задача 1", "Описание 1", null, TaskStatus.NEW, Duration.ofDays(2), LocalDateTime.of(2000, 5, 1, 1, 1));
        Task task2 = new Task("Задача 2", "Описание 2", null, TaskStatus.NEW, Duration.ofDays(2), null);
        Task task3 = new Task("Задача 2", "Описание 2", null, TaskStatus.NEW, Duration.ofDays(2), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика", null, TaskStatus.NEW, taskManager);
        int epic1Id = taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", null, epic1Id, TaskStatus.NEW, Duration.ofDays(3), LocalDateTime.of(2001, 1, 1, 1, 1));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", null, epic1Id, TaskStatus.IN_PROGRESS, Duration.ofDays(3), LocalDateTime.of(2005, 3, 2, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        System.out.println("Отсортированные задачи:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        System.out.println("Задачи до загрузки:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nЗадачи после загрузки из файла:");
        System.out.println(loadedManager.getAllTasks());
        System.out.println(loadedManager.getAllEpic());
        System.out.println(loadedManager.getAllSubtask());

    }
}