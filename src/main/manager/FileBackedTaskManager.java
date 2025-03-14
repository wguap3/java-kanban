package main.manager;

import main.exception.IntersectionTimeException;
import main.exception.ManagerSaveException;
import main.formatter.FormatterUtil;
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
                    writer.write(FormatterUtil.toString(task));
                    writer.newLine();
                }
                for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                    final Epic epic = entry.getValue();
                    writer.write(FormatterUtil.toString(epic));
                    writer.newLine();
                }
                for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                    final Subtask subtask = entry.getValue();
                    writer.write(FormatterUtil.toString(subtask));
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
                final Task task = FormatterUtil.fromString(line, taskManager);
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
