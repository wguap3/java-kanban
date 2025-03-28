package main.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String describe;
    private Integer id;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String describe, Integer id, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.describe = describe;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }


    public String getName() {
        return name;
    }

    public String getDescribe() {
        return describe;
    }

    public Integer getId() {

        return id;
    }

    public TaskStatus getStatus() {

        return status;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public void setStatus(TaskStatus status) {

        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration == null || startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) && Objects.equals(describe, otherTask.describe) && Objects.equals(id, otherTask.id) && Objects.equals(status, otherTask.status);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (describe != null) {
            hash = hash + describe.hashCode();
        }
        hash = hash * 31;

        if (id != null) {
            hash = hash + id.hashCode();
        }
        hash = hash * 31;

        if (status != null) {
            hash = hash + status.hashCode();
        }
        return hash;

    }
}
