package main.task;

import main.manager.HistoryManager;
import main.manager.Managers;
import main.manager.TaskManager;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String describe, Integer id, TaskStatus status) {
        super(name, describe, id, null);
    }

    private Epic(String name, String describe, Integer id, TaskStatus status, ArrayList<Integer> subtaskIds) {
        super(name, describe, id, status);
        this.subtaskIds = subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void removeSubtask(int subtaskId) {
        if (subtaskIds.contains(subtaskId)) {
            subtaskIds.remove(Integer.valueOf(subtaskId)); // Удаляем по значению
        }
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", describe='" + getDescribe() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtask" + subtaskIds.toString() +
                '}';
    }
}