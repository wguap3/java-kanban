package main.server.handlers;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.IntersectionTimeException;
import main.exception.NotFoundException;
import main.manager.TaskManager;
import main.task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /epics запроса от клиента.");
        switch (method) {
            case "GET":
                handleGetEpics(httpExchange);
                break;
            case "POST":
                handlePostEpics(httpExchange);
                break;
            case "DELETE":
                handleDeleteEpics(httpExchange);
                break;
            default:
                sendText(httpExchange, "Некорректный метод!", 400);
        }

    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        try {
            if (splitStrings.length == 2) {
                List<Epic> epics = taskManager.getAllEpic();
                String jsonString = gson.toJson(epics);
                if (jsonString.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }
                sendText(exchange, jsonString, 200);
            } else if (splitStrings.length == 3) {
                int epicId = Integer.parseInt(splitStrings[2]);
                Epic epic = taskManager.getIdEpic(epicId);
                if (epic != null) {
                    sendText(exchange, gson.toJson(epic), 200);
                } else {
                    sendNotFound(exchange);
                }
            } else if (splitStrings.length == 4) {
                int epicId = Integer.parseInt(splitStrings[2]);
                Epic epic = taskManager.getIdEpic(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                }
                String jsonString = gson.toJson(taskManager.getSubtaskOfEpic(epicId));
                if (jsonString.isEmpty()) {
                    sendNotFound(exchange);
                }
                sendText(exchange, jsonString, 200);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID эпика", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Эпика с таким ID нет.", 400);
        }
    }

    private void handlePostEpics(HttpExchange exchange) throws IOException {
        InputStream stream = exchange.getRequestBody();
        String strEpic = new String(stream.readAllBytes());

        if (strEpic.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        Epic epic;
        epic = gson.fromJson(strEpic, Epic.class);
        epic.setTaskManager(taskManager);

        try {
            taskManager.addEpic(epic);
            sendText(exchange, "Эпик успешно добавлен", 201);
        } catch (IntersectionTimeException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        try {
            if (splitStrings.length == 3) {
                int epicId = Integer.parseInt(splitStrings[2]);
                taskManager.removeEpic(epicId);
                sendText(exchange, "Удаление прошло успешно", 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный ID задачи", 400);
        } catch (NotFoundException e) {
            sendText(exchange, "Эпика с таким ID нет.", 400);
        }
    }
}
