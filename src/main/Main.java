package main;

import main.manager.FileBackedTaskManager;
import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Задача 1", "Описание 1", null, TaskStatus.NEW);
        taskManager.addTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика", null, TaskStatus.NEW);
        int epic1Id = taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", null, epic1Id, TaskStatus.NEW);
        taskManager.addSubtask(subtask1);

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