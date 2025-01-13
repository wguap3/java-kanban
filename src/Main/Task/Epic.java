package Main.Task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String describe, Integer id, TaskStatus status) {
        super(name, describe, id, null);
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
        subtaskIds.remove(subtaskId);
    }

    public void cleanSubtaskIds (){
        subtaskIds.clear();
    }

    public void addSubtaskId(int id){
        subtaskIds.add(id);
    }




    @Override
    public String toString() {
        return "Main.Task.Epic{" +
                "name='" + getName() + '\'' +
                ", describe='" + getDescribe() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtask" + subtaskIds.toString() +
                '}';
    }


}
