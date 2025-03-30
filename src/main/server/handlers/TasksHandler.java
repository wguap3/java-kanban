package main.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.IntersectionTimeException;
import main.exception.NotFoundException;
import main.manager.TaskManager;
import main.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /tasks запроса от клиента.");
        switch (method) {
            case "GET":
                handleGetTasks(httpExchange);
                break;
            case "POST":
                handlePostTasks(httpExchange);
                break;
            case "DELETE":
                handleDeleteTasks(httpExchange);
                break;
            default:
                sendText(httpExchange, "Некорректный метод!", 400);
        }

    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        try {
            if (splitStrings.length == 2) { // Запрос /tasks
                String jsonString = gson.toJson(taskManager.getAllTasks());
                if (jsonString.isEmpty()) {
                    sendNotFound(exchange);
                }
                sendText(exchange, jsonString, 200);
            } else if (splitStrings.length == 3) {
                int taskId = Integer.parseInt(splitStrings[2]);
                Task task = taskManager.getIdTask(taskId);
                if (task != null) {
                    sendText(exchange, gson.toJson(task), 200);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID задачи.", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Задачи с таким ID нет.", 400);
        }
    }

    private void handlePostTasks(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strTask = new String(stream.readAllBytes());
        if (strTask.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        Task task;
        try {
            task = gson.fromJson(strTask, Task.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }
        try {
            if (task.getId() == null) {
                try {
                    taskManager.addTask(task);
                    sendText(exchange, "Подзадача успешно добавлена", 201);
                } catch (IntersectionTimeException e) {
                    sendHasInteractions(exchange);
                }

            } else {
                taskManager.updateTask(task);
                sendText(exchange, "Подзадача успешно обновлена", 201);
            }
        } catch (InterruptedIOException e) {
            sendHasInteractions(exchange);
        }

    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        try {
            if (splitStrings.length == 3) {
                int taskId = Integer.parseInt(splitStrings[2]);
                taskManager.removeTask(taskId);
                sendText(exchange, "Удаление прошло успешно", 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID задачи", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Задачи с таким ID нет.", 400);
        }
    }
}