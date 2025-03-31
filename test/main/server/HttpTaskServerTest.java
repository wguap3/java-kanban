package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.exception.NotFoundException;
import main.manager.InMemoryTaskManager;
import main.server.adapter.DurationAdapter;
import main.server.adapter.EpicAdapter;
import main.server.adapter.LocalDateTimeAdapter;
import main.task.Epic;
import main.task.Subtask;
import main.task.Task;
import main.task.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer server = new HttpTaskServer(taskManager);
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .setPrettyPrinting()
            .create();

    @BeforeEach
    public void setUp() {
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", null, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", null, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(2, tasksFromServer.size(), "Неверное количество задач");
        Task serverTask1 = tasksFromServer.get(0);
        Task serverTask2 = tasksFromServer.get(1);
        assertEquals("Task 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Task 2", serverTask2.getName(), "Неверное имя второй задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", null, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", null, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Task tasksFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals("Task 1", tasksFromServer.getName(), "Неверное имя первой задачи");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 1", null, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", 1, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", 2, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertThrows(NotFoundException.class, () -> taskManager.getIdTask(1),
                "Задачи не удалена.");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", null, null);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(2, tasksFromServer.size(), "Неверное количество задач");
        Task serverTask1 = tasksFromServer.get(0);
        Task serverTask2 = tasksFromServer.get(1);
        assertEquals("Subtask 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Subtask 2", serverTask2.getName(), "Неверное имя второй задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", null, null);
        int epicId = taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epicId, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epicId, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Task tasksFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals("Subtask 1", tasksFromServer.getName(), "Неверное имя первой задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", null, null);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = taskManager.getAllSubtask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", null, null);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subask 1", "Testing subtask 1", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subask 2", "Testing subtask 2", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertThrows(NotFoundException.class, () -> taskManager.getIdSubtask(1),
                "Задачи не удалена.");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic2.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(2, tasksFromServer.size(), "Неверное количество задач");
        Epic serverTask1 = tasksFromServer.get(0);
        Epic serverTask2 = tasksFromServer.get(1);
        assertEquals("Epic 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Epic 2", serverTask2.getName(), "Неверное имя второй задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic2.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Epic tasksFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic 2", tasksFromServer.getName(), "Неверное имя первой задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", null, null);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", null, epic.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> tasksFromManager = taskManager.getAllEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic2.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertThrows(NotFoundException.class, () -> taskManager.getIdSubtask(1),
                "Задача не удалена.");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(2, tasksFromServer.size(), "Неверное количество задач");
        Task serverTask1 = tasksFromServer.get(0);
        Task serverTask2 = tasksFromServer.get(1);
        assertEquals("Subtask 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Subtask 2", serverTask2.getName(), "Неверное имя второй задачи");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic2.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Task task1 = new Task("Task 1", "Testing task 1", null, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(1999, 1, 1, 1, 11));
        Task task2 = new Task("Task 2", "Testing task 2", null, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getIdTask(5);
        taskManager.getIdSubtask(3);
        taskManager.getIdEpic(1);
        taskManager.getIdSubtask(4);
        taskManager.getIdEpic(2);
        taskManager.getIdTask(6);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(6, tasksFromServer.size(), "Неверное количество задач");
        Task serverTask1 = tasksFromServer.get(0);
        Task serverTask2 = tasksFromServer.get(1);
        assertEquals("Task 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Subtask 1", serverTask2.getName(), "Неверное имя второй задачи");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Testing epic 1", null, null);
        Epic epic2 = new Epic("Epic 2", "Testing epic 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask 1", "Testing subtask 1", null, epic1.getId(), TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Testing subtask 2", null, epic2.getId(), TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        Task task1 = new Task("Task 1", "Testing task 1", null, TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(1999, 1, 1, 1, 11));
        Task task2 = new Task("Task 2", "Testing task 2", null, TaskStatus.NEW, Duration.ofMinutes(9), LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        Type taskListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksFromServer = gson.fromJson(response.body(), taskListType);
        assertEquals(4, tasksFromServer.size(), "Неверное количество задач");
        Task serverTask1 = tasksFromServer.get(0);
        Task serverTask2 = tasksFromServer.get(1);
        assertEquals("Task 1", serverTask1.getName(), "Неверное имя первой задачи");
        assertEquals("Subtask 2", serverTask2.getName(), "Неверное имя второй задачи");
    }

}
