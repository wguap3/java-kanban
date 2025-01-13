package Main.Manager;

import Main.Task.Epic;
import Main.Task.Subtask;
import Main.Task.Task;
import Main.Task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int count = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private  HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private int generateId() {
        count++;
        return count;
    }

    public Integer addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public Integer addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());//сделай метод
        updateStatus(epicId);
        return id;
    }

    public void addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateStatus(epic.getId());
        }
        subtasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
            }
        }
    }

    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public Task getIdTask(int id) {
        return tasks.get(id);
    }

    public Subtask getIdSubtask(int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateStatus(epicId);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescribe(epic.getDescribe());

    }

    public ArrayList<Subtask> getSubtaskOfEpic(int epicId) {
        ArrayList<Subtask> listSub = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int id : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(id);
                listSub.add(subtask);
            }
        }
        return listSub;
    }

    private void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
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





