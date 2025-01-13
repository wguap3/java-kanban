package Main.Task;

import java.util.Objects;

public class Task {
    private String name;
    private String describe;
    private Integer id;
    private TaskStatus status;

    public Task(String name, String describe, Integer id, TaskStatus status) {
        this.name = name;
        this.describe = describe;
        this.id = id;
        this.status = status;
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

    @Override
    public String toString() {
        return "Main.Task.Task{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return  Objects.equals(name, otherTask.name) &&
                Objects.equals(describe, otherTask.describe) &&
                Objects.equals(id, otherTask.id) &&
                Objects.equals(status, otherTask.status);
    }

    @Override
    public  int hashCode(){
        int hash = 17;
        if(name != null){
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if(describe != null){
            hash = hash + describe.hashCode();
        }
        hash = hash * 31;

        if(id != null){
            hash = hash + id.hashCode();
        }
        hash = hash * 31;

        if(id != null){
            hash = hash + status.hashCode();
        }
        return hash;

    }


}
