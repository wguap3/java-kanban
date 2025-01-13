import java.util.ArrayList;

public class Epic extends Task {
    private int epicId;
    ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String describe, Integer id, TaskStatus status) {
        super(name, describe, id, null);
    }


    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", id=" + epicId +
                ", status=" + getStatus() +
                ", subtask" + subtaskIds.toString() +
                '}';
    }


}
