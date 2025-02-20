package main.manager;

import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = new File(file.getAbsolutePath());
    }

    public void save() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("id,type,name,status,description,epic\n"); // Заголовок CSV

                for (Task task : getAllTasks()) {
                    writer.write(task.toString() + "\n");
                }
                for (Epic epic : getAllEpic()) {
                    writer.write(epic.toString() + "\n");
                }
                for (Subtask subtask : getAllSubtask()) {
                    writer.write(subtask.toString() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Integer id = Integer.parseInt(fields[0]);
                String type = fields[1];
                String name = fields[2];
                TaskStatus status = TaskStatus.valueOf(fields[3]);
                String describe = fields[4];

                switch (type) {
                    case "TASK":
                        Task task = new Task(name, describe, id, status);
                        manager.addTask(task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(name, describe, id, status);
                        manager.addEpic(epic);
                        break;
                    case "SUBTASK":
                        int epicId = Integer.parseInt(fields[5]);
                        Subtask subtask = new Subtask(name, describe, id, epicId, status);
                        manager.addSubtask(subtask);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки из файла: " + e.getMessage());
        }
        return manager;
    }

    @Override
    public Integer addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
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
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
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
