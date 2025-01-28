package main.task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String describe, Integer id, Integer epicId, TaskStatus status) {
        super(name, describe, id, status);
        this.epicId = epicId;
    }

    public Subtask getShapshot() {
        return new Subtask(this.getName(), this.getDescribe(), this.getId(), this.getEpicId(), this.getStatus());
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
                '}';
    }
}