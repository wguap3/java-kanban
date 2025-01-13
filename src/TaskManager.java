import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int count = 0;
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();

    private int addId() {
        count++;
        return count;
    }

    public void addTask(Task task) {
        int id = addId();
        task.setId(id);
        taskMap.put(id, task);
    }

    public void addSubtask(Subtask subtask) {
        int id = addId();
        subtask.setId(id);
        subtaskMap.put(id, subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(id);
        }

    }

    public void addEpic(Epic epic) {
        int id = addId();
        epic.setEpicId(id);
        epicMap.put(id, epic);
    }

    public void removeAllTask() {
        taskMap.clear();
    }

    public void removeAllSubtask() {
        subtaskMap.clear();
    }

    public void removeAllEpic() {
        epicMap.clear();
    }

    public void removeTask(int id) {
        taskMap.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtaskMap.remove(id);
        if (subtask != null) {
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
            }
        }
    }

    public void removeEpic(int id) {
        Epic epic = epicMap.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtaskMap.remove(subtaskId);
            }
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    public Task getIdTask(int id) {
        return taskMap.get(id);
    }

    public Subtask getIdSubtask(int id) {
        return subtaskMap.get(id);
    }

    public Epic getIdEpic(int id) {
        return epicMap.get(id);
    }

    public void updateTask(Task task) {
        int taskId = task.getId();
        if (taskMap.containsKey(taskId)) {
            taskMap.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            updateStatus(epic.getEpicId());
        }
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getEpicId();
        if (epicMap.containsKey(epicId)) {
            epicMap.put(epicId, epic);
        }
    }

    public ArrayList<Subtask> getSubtaskOfEpic(int epicId) {
        ArrayList<Subtask> listSub = new ArrayList<>();
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            for (int id : epic.subtaskIds) {
                Subtask subtask = subtaskMap.get(id);
                listSub.add(subtask);
            }
        }
        return listSub;
    }

    public void updateStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subtaskId = epic.getSubtaskIds();
        if (subtaskId.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allDone = false;
        boolean inProgress = false;
        boolean isNew = false;
        for (int subId : subtaskId) {
            Subtask subtask = getIdSubtask(subId);
            if (subtask != null) {
                if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                    inProgress = true;
                }

                if (subtask.getStatus() == TaskStatus.DONE) {
                    allDone = true;
                }
                if (subtask.getStatus() == TaskStatus.NEW) {
                    isNew = true;
                }

            }
        }

        if (allDone && !inProgress && !isNew) {
            epic.setStatus(TaskStatus.DONE);
        } else if (inProgress || (allDone && isNew)) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }


}





