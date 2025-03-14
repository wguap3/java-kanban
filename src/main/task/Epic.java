package main.task;

import main.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;
    private InMemoryTaskManager taskManager;

    public Epic(String name, String describe, Integer id, TaskStatus status) {
        super(name, describe, id, null, null, null);
    }

    public Epic(String name, String describe, Integer id, TaskStatus status, Duration duration, LocalDateTime localDateTime) {
        super(name, describe, id, null, duration, localDateTime);
    }

    public Epic(String name, String describe, Integer id, TaskStatus status, InMemoryTaskManager taskManager) {
        super(name, describe, id, status, null, null);
        this.taskManager = taskManager;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Duration getDuration() {
        if (subtaskIds.isEmpty()) {
            return Duration.ZERO;
        }
        return subtaskIds.stream()
                .map(taskManager::getIdSubtask)
                .filter(subtask -> subtask != null)
                .map(Subtask::getDuration)
                .filter(duration -> duration != null)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime() {
        return subtaskIds.stream()
                .map(taskManager::getIdSubtask)
                .filter(subtask -> subtask != null)
                .map(Subtask::getStartTime)
                .filter(startTime -> startTime != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subtaskIds.stream()
                .map(taskManager::getIdSubtask)
                .filter(subtask -> subtask != null)
                .map(Subtask::getEndTime)
                .filter(endTime -> endTime != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
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
            subtaskIds.remove(Integer.valueOf(subtaskId));
        }
    }

    public TaskType getType() {
        return TaskType.EPIC;
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}