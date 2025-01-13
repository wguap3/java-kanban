package Main.Task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String describe, Integer id, Integer epicId, TaskStatus status) {
        super(name, describe, id, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Main.Task.Subtask{" +
                "name='" + getName() + '\'' +
                ", describe='" + getDescribe() + '\'' +
                ", id=" + getId() +
                ", idEpic=" + getEpicId() +
                ", status=" + getStatus() +
                '}';
    }
}