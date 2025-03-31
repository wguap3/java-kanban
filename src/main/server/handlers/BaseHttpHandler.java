package main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.server.adapter.DurationAdapter;
import main.server.adapter.EpicAdapter;
import main.server.adapter.LocalDateTimeAdapter;
import main.task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final static String GET_STATUS = "GET";
    protected final static String POST_STATUS = "POST";
    protected final static String DELETE_STATUS = "DELETE";
    protected Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .setPrettyPrinting()
            .create();

    protected void sendText(HttpExchange httpExchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.getResponseBody().close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Не найдено", 404);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Задачи пересекаются", 406);
    }
}
