package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /history запроса от клиента.");
        switch (method) {
            case "GET":
                handleGetHistory(httpExchange);
                break;
            default:
                sendText(httpExchange, "Некорректный метод!", 400);
        }

    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        if (splitStrings.length == 2) { // Запрос /tasks
            String jsonString = gson.toJson(taskManager.getHistory());
            if (jsonString.isEmpty()) {
                sendNotFound(exchange);
            }
            sendText(exchange, jsonString, 200);
        } else {
            sendNotFound(exchange);
        }

    }

}