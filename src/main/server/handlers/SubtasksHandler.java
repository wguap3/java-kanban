package main.server.handlers;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.IntersectionTimeException;
import main.exception.NotFoundException;
import main.manager.TaskManager;
import main.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /subtasks запроса от клиента.");
        switch (method) {
            case GET_STATUS:
                handleGetSubtasks(httpExchange);
                break;
            case POST_STATUS:
                handlePostSubtasks(httpExchange);
                break;
            case DELETE_STATUS:
                handleDeleteSubtasks(httpExchange);
                break;
            default:
                sendText(httpExchange, "Некорректный метод!", 400);
        }

    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        try {
            if (splitStrings.length == 2) { // Запрос /tasks
                String jsonString = gson.toJson(taskManager.getAllSubtask());
                if (jsonString.isEmpty()) {
                    sendNotFound(exchange);
                }
                sendText(exchange, jsonString, 200);
            } else if (splitStrings.length == 3) {
                int subtaskId = Integer.parseInt(splitStrings[2]);
                Subtask subtask = taskManager.getIdSubtask(subtaskId);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask), 200);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID задачи", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Подзадачи с таким ID нет.", 400);
        }
    }

    private void handlePostSubtasks(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strSubtask = new String(stream.readAllBytes());
        if (strSubtask.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        Subtask subtask;
        try {
            subtask = gson.fromJson(strSubtask, Subtask.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }
        try {
            if (subtask.getId() == null) {
                try {
                    taskManager.addSubtask(subtask);
                    sendText(exchange, "Подзадача успешно добавлена", 201);
                } catch (IntersectionTimeException e) {
                    sendHasInteractions(exchange);
                }

            } else {
                try {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "Подзадача успешно обновлена", 201);
                } catch (NotFoundException e) {
                    sendText(exchange, "При обновление, подзадача с таким ID не найдена.", 400);
                }
            }
        } catch (InterruptedIOException e) {
            sendHasInteractions(exchange);
        }

    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        try {
            if (splitStrings.length == 3) {
                int subtaskId = Integer.parseInt(splitStrings[2]);
                taskManager.removeSubtask(subtaskId);
                sendText(exchange, "Удаление подзадачи прошло успешно", 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID подзадачи", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Подзадачи с таким ID нет.", 400);
        }
    }
}
