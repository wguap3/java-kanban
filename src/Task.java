public class Task {
    String name;
    String describe;
    private Integer id;
    private TaskStatus status;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Task(String name, String describe, Integer id, TaskStatus status) {
        this.name = name;
        this.describe = describe;
        this.id = id;
        this.status = status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }


}
