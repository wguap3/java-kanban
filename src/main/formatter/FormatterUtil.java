package main.formatter;

import main.manager.InMemoryTaskManager;
import main.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatterUtil {
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
}
