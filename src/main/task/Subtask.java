package main.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;


    public Subtask(String name, String describe, Integer id, Integer epicId, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, describe, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", describe='" + getDescribe() + '\'' +
                ", id=" + getId() +
                ", idEpic=" + getEpicId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}