package main.manager;

import main.exception.IntersectionTimeException;
import main.exception.ManagerSaveException;
import main.task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime,endTime";
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public FileBackedTaskManager(File file) {
        this.file = new File(file.getAbsolutePath());
    }

    public static String toString(Task task) {
        String durationStr = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTimeStr = (task.getStartTime() != null) ? task.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        String endTimeStr = (task.getEndTime() != null) ? task.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        String epicIdStr = (task.getType().equals(TaskType.SUBTASK)) ? String.valueOf(((Subtask) task).getEpicId()) : "";

        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescribe() + "," +
                epicIdStr + "," +
                durationStr + "," +
                startTimeStr + "," +
                endTimeStr;
    }


    public static Task fromString(String line, InMemoryTaskManager taskManager) {
        String[] fields = line.split(",");
        Integer id = fields.length > 0 && !fields[0].isEmpty() ? Integer.parseInt(fields[0]) : null;
        String type = fields.length > 1 && !fields[1].isEmpty() ? fields[1] : null;
        String name = fields.length > 2 && !fields[2].isEmpty() ? fields[2] : null;
        TaskStatus status = fields.length > 3 && !fields[3].isEmpty() ? TaskStatus.valueOf(fields[3]) : null;
        String describe = fields.length > 4 && !fields[4].isEmpty() ? fields[4] : null;
        Duration duration = (fields.length > 6 && !fields[6].isEmpty()) ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;
        LocalDateTime startTime = (fields.length > 7 && !fields[7].isEmpty())
                ? LocalDateTime.parse(fields[7], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;
        LocalDateTime endTime = (fields.length > 8 && !fields[8].isEmpty())
                ? LocalDateTime.parse(fields[8], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;

        switch (type) {
            case "TASK":
                return new Task(name, describe, id, status, duration, startTime);
            case "EPIC":
                return new Epic(name, describe, id, status, taskManager);
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(name, describe, id, epicId, status, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public void save() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(HEADER);
                writer.newLine();
                for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                    final Task task = entry.getValue();
                    writer.write(toString(task));
                    writer.newLine();
                }
                for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                    final Epic epic = entry.getValue();
                    writer.write(toString(epic));
                    writer.newLine();
                }
                for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                    final Subtask subtask = entry.getValue();
                    writer.write(toString(subtask));
                    writer.newLine();
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно сохранить в файл:" + file.getName(), e);
        }
    }


    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line, taskManager);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                taskManager.addAnyTask(task);
            }
            for (Map.Entry<Integer, Subtask> e : taskManager.subtasks.entrySet()) {
                final Subtask subtask = e.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
                taskManager.updateStatus(subtask.getEpicId());
            }
            taskManager.setCount(generatorId);
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read form file: " + file.getName(), e);
        }
        return taskManager;
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


    @Override
    public Integer addTask(Task task) {
        validateTaskOverlap(task);
        int id = super.addTask(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        save();
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        validateTaskOverlap(subtask);
        int id = super.addSubtask(subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        save();
        return id;
    }


    @Override
    public Integer addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        Task taskToRemove = tasks.get(id);
        if (taskToRemove != null) {
            prioritizedTasks.remove(taskToRemove);
        }
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        Subtask subtaskToRemove = subtasks.get(id);
        if (subtaskToRemove != null) {
            prioritizedTasks.remove(subtaskToRemove);
        }
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }
}
