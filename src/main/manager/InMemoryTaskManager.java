package main.manager;

import main.exception.IntersectionTimeException;
import main.exception.NotFoundException;
import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public Integer addTask(Task task) {
        int id = generateId();
        task.setId(id);
        validateTaskOverlap(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        validateTaskOverlap(subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с таким id не найден.");
        }
        if (subtask.getId() != null && subtask.getId() == epicId) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epic.addSubtaskId(subtask.getId());
        updateStatus(epicId);
        return id;
    }

    @Override
    public Integer addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epic.setTaskManager(this);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtask() {

        epics.values().forEach(epic -> {
            epic.cleanSubtaskIds();
            updateStatus(epic.getId());
        });
        subtasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeTask(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        } else {
            throw new NotFoundException("Задача с таким id не найдена.");
        }
    }

    @Override
    public void removeSubtask(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадачи с таким ID не существует.");
        }
        subtasks.remove(id);
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик для подзадачи не найден.");
        }
        epic.removeSubtask(id);
        updateStatus(epicId);
    }

    @Override
    public void removeEpic(int id) throws NotFoundException {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
        } else {
            throw new NotFoundException("Эпика с таким ID не найден.");
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

    @Override
    public Task getIdTask(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с таким id не найдена.");
        }
        historyManager.addTask(task);
        return task;
    }

    @Override
    public Subtask getIdSubtask(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с таким id не найдена.");
        }
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public Epic getIdEpic(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с таким id не найден.");
        }
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) throws NotFoundException {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            throw new NotFoundException("Задача с таким id не найдена.");
        }
        tasks.put(id, task);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws NotFoundException {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            throw new NotFoundException("Подзадача с таким id не найдена.");
        }
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с таким id не найден.");
        }
        subtasks.put(id, subtask);
        updateStatus(epicId);
    }


    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            throw new NotFoundException("Эпик с таким id не найден.");
        }
        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);

    }

    @Override
    public ArrayList<Subtask> getSubtaskOfEpic(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public void validateTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null) {
            return;
        }

        for (Task existingTask : prioritizedTasks) {
            if (isTasksOverlap(newTask, existingTask)) {
                throw new IntersectionTimeException("Задача пересекается по времени с другой задачей.");
            }
        }
    }

    protected int generateId() {
        count++;
        return count;
    }


    protected void updateStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subtaskId = epic.getSubtaskIds();
        if (subtaskId.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allDone = subtaskId.stream()
                .map(this::getIdSubtask)
                .filter(Objects::nonNull)
                .anyMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        boolean inProgress = subtaskId.stream()
                .map(this::getIdSubtask)
                .filter(Objects::nonNull)
                .anyMatch(subtask -> subtask.getStatus() == TaskStatus.IN_PROGRESS);

        boolean isNew = subtaskId.stream()
                .map(this::getIdSubtask)
                .filter(Objects::nonNull)
                .anyMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);


        if (allDone && !inProgress && !isNew) {
            epic.setStatus(TaskStatus.DONE);
        } else if (inProgress || (allDone && isNew)) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}