package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /prioritized запроса от клиента.");
        switch (method) {
            case GET_STATUS:
                handleGetPrioritized(httpExchange);
                break;
            default:
                sendText(httpExchange, "Некорректный метод!", 400);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        if (splitStrings.length == 2) {
            String jsonString = gson.toJson(taskManager.getPrioritizedTasks());
            if (jsonString.isEmpty()) {
                sendNotFound(exchange);
            }
            sendText(exchange, jsonString, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}
