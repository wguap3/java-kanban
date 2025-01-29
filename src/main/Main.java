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
        Task task3 = new Task("Задача 3", "Описание задачи 3", null, TaskStatus.DONE);
        Task task4 = new Task("Задача 4", "Описание задачи 4", null, TaskStatus.IN_PROGRESS);
        Task task5 = new Task("Задача 5", "Описание задачи 5", null, TaskStatus.DONE);
        Task task6 = new Task("Задача 6", "Описание задачи 6", null, TaskStatus.IN_PROGRESS);
        Task task7 = new Task("Задача 7", "Описание задачи 6", 1, TaskStatus.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task6);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", null, null);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", null, epic1.getId(), TaskStatus.DONE);
        taskManager.addSubtask(subtask1);

        System.out.println("Tasks:");
        System.out.println(taskManager.getAllTasks());

        System.out.println("Subtasks:");
        System.out.println(taskManager.getAllSubtask());

        System.out.println("Epics:");
        System.out.println(taskManager.getAllEpic());

        taskManager.getIdEpic(7);
        //у нас получается в истории происходит дупликация sabtask из за того что при возвращении эпика subtask тоже возвращается?
        taskManager.getIdTask(2);
        taskManager.getIdTask(3);
        taskManager.getIdTask(4);
        taskManager.getIdTask(5);
        taskManager.getIdTask(6);
        taskManager.getIdTask(1);
        taskManager.getIdTask(2);
        taskManager.getIdTask(1);
        taskManager.updateTask(task7);
        taskManager.getIdTask(1);
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}