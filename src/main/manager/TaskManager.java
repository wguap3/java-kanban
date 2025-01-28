package main.manager;

import main.task.Epic;
import main.task.Subtask;
import main.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Integer addTask(Task task);

    Integer addSubtask(Subtask subtask);

    Integer addEpic(Epic epic);

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    Task getIdTask(int id);

    Subtask getIdSubtask(int id);

    Epic getIdEpic(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    ArrayList<Subtask> getSubtaskOfEpic(int epicId);

    List<Task> getHistory();
}