package main.task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String describe, Integer id, Integer epicId, TaskStatus status) {
        super(name, describe, id, status);
        this.epicId = epicId;
    }
    public TaskType getType(){
        return TaskType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), getType(), getName(), getStatus(), getDescribe(), epicId);
    }
}