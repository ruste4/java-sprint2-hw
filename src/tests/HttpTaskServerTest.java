package tests;

import com.google.gson.*;
import components.Status;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import server.exceptions.RequestException;
import server.exceptions.TaskException;
import server.typeAdapters.DurationAdapter;
import server.typeAdapters.ExceptionAdapter;
import server.typeAdapters.LocalDateTimeAdapter;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;
import tasksmanagers.Managers;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager taskManager;
    private static HttpClient httpClient;
    private static String url;
    private static EpicTask epic;
    private static Subtask subtask;
    private static MonoTask monotask;
    private static Gson gson;

    @BeforeAll
    public static void httpTaskServerTestBeforeAll() throws IOException {
        epic = new EpicTask(1, "Epic", "");
        subtask = new Subtask(2, "Subtask", "", 1, Status.NEW);
        monotask = new MonoTask(3, "Monotask", "", Status.NEW);

        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskManager.addNewTask(epic);
        taskManager.addNewTask(subtask);
        taskManager.addNewTask(monotask);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        httpClient = HttpClient.newHttpClient();
        url = "http://localhost:8080";
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskException.class, new ExceptionAdapter())
                .registerTypeAdapter(RequestException.class, new ExceptionAdapter())
                .setPrettyPrinting()
                .create();
    }

    @BeforeEach
    public void beforeEachHttpTaskServerTest() {
        taskManager.updateTask(epic, epic.getId());
        taskManager.updateTask(subtask, subtask.getId());
        taskManager.updateTask(monotask, monotask.getId());
    }

    @DisplayName("Должен вернуть EpicTask при GET-запросе с id epic-задачи по адресу tasks/task?id=1")
    @Test
    public void shouldBeReturnEpicTaskByGetRequestOnTaskHandler() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(request, handler);
        EpicTask epicFromServer = gson.fromJson(response.body(), EpicTask.class);
        assertEquals(epic, epicFromServer);
    }

    @DisplayName("Должен вернуть Subtask при GET-запросе с id epic-задачи по адресу tasks/task?id=2")
    @Test
    public void shouldBeReturnSubtaskByGetRequestOnTaskHandler() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(request, handler);
        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask, subtaskFromServer);
    }

    @DisplayName("Должен вернуть Monotask при GET-запросе с id epic-задачи по адресу tasks/task?id=3")
    @Test
    public void shouldBeReturnMonotaskByGetRequestOnTaskHandler() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(request, handler);
        MonoTask monotaskFromServer = gson.fromJson(response.body(), MonoTask.class);
        assertEquals(monotask, monotaskFromServer);
    }

    @DisplayName("Должен вернуть статус 400 и информацию об ошибке, если задача не была найдена по id")
    @Test
    public void shouldBeReturnCode400AndErrorInfoIfTaskNotFoundById() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task?id=111");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(request, handler);
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();

        Assertions.assertAll(
                () -> assertEquals(jsonObj.get("status").getAsInt(), 400),
                () -> assertEquals(jsonObj.get("message").getAsString(), "Задача c id=111 не найдена"),
                () -> Assertions.assertNotNull(jsonObj.get("stackTrace"))
        );
    }

    @DisplayName("Должен вернуть статус 400 и информацию об ошибке, не передан параметр запроса id")
    @Test
    public void shouldBeReturnCode400AndErrorInfoIfRequestParameterIdNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task?ids=111");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObj = JsonParser.parseString(response.body()).getAsJsonObject();

        Assertions.assertAll(
                () -> assertEquals(jsonObj.get("status").getAsInt(), 400),
                () -> assertEquals(jsonObj.get("message").getAsString(), "Параметр id в строке запроса не был найден"),
                () -> Assertions.assertNotNull(jsonObj.get("stackTrace"))
        );
    }

    @DisplayName("Должен обновить Epic при POST запросе по адресу tasks/task c загаловком X-action:update")
    @Test
    public void shouldBeUpdateEpicTaskByPostRequestWithHeaderXActionUpdate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        EpicTask updatedEpic = new EpicTask(epic.getId(), "updatedEpic", "");

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(updatedEpic));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "update")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(epic.getId()).getTitle(), updatedEpic.getTitle());
    }

    @DisplayName("Должен обновить Subtask при POST запросе по адресу tasks/task c загаловком X-action:update")
    @Test
    public void shouldBeUpdateSubtaskTaskByPostRequestWithHeaderXActionUpdate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        Subtask updatedSubtask = new Subtask(subtask.getId(), "updated subtask", "", epic.getId(), Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(updatedSubtask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "update")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(subtask.getId()).getTitle(), updatedSubtask.getTitle());
    }

    @DisplayName("Должен обновить Monotask при POST запросе по адресу tasks/task c загаловком X-action:update")
    @Test
    public void shouldBeUpdateMonotaskTaskByPostRequestWithHeaderXActionUpdate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        MonoTask updatedMonoTask = new MonoTask(monotask.getId(), "updated monotask", "", Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(updatedMonoTask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "update")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(monotask.getId()).getTitle(), updatedMonoTask.getTitle());
    }

    @DisplayName("Должен добавить Monotask при POST запросе по адресу tasks/task c загаловком X-action:create")
    @Test
    public void shouldBeAddMonotaskTaskByPostRequestWithHeaderXActionCreate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        MonoTask newMonotask = new MonoTask(222, "New monotask", "", Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newMonotask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "create")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(newMonotask.getId()).getTitle(), newMonotask.getTitle());
    }

    @DisplayName("Должен добавить Epic при POST запросе по адресу tasks/task c загаловком X-action:create")
    @Test
    public void shouldBeAddEpicTaskByPostRequestWithHeaderXActionCreate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        EpicTask newEpic = new EpicTask(333, "New epic", "");

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "create")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(newEpic.getId()).getTitle(), newEpic.getTitle());
    }

    @DisplayName("Должен добавить Subtask при POST запросе по адресу tasks/task c загаловком X-action:create")
    @Test
    public void shouldBeAddSubtaskTaskByPostRequestWithHeaderXActionCreate() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        Subtask newSubtask = new Subtask(444, "New subtask", "", epic.getId(), Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("X-action", "create")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(taskManager.getTaskById(newSubtask.getId()).getTitle(), newSubtask.getTitle());
    }

    @DisplayName("Должен вернуть 400 ошибку и описание, если заголовка X-action не было")
    @Test
    public void shouldBeReturnCode400AndErrorInfoIfHeaderXActionNotFound() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        Subtask newSubtask = new Subtask(444, "New subtask", "", epic.getId(), Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body().toString()).getAsJsonObject();

        Assertions.assertAll(
                () -> assertEquals(jsonObject.get("status").getAsInt(), 400),
                () -> assertEquals(jsonObject.get("message").getAsString(), "Нет заголовка 'X-action'")
        );
    }

    @DisplayName("Должен вернуть 400 ошибку и описание, если происходит ошибка при добавлении")
    @Test
    public void shouldBeReturnCode400AndInfoWhenAddingAnError() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        Subtask newSubtask = new Subtask(1, "New subtask", "", epic.getId(), Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .header("X-action", "create")
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body().toString()).getAsJsonObject();

        Assertions.assertAll(
                () -> assertEquals(jsonObject.get("status").getAsInt(), 400),
                () -> assertEquals(
                        jsonObject.get("message").getAsString(),
                        "Не получилось добавить новую задачу с id=1, возможно, она была добавлена ранее"
                )
        );
    }

    @DisplayName("Должен вернуть 400 ошибку и описание, если происходит ошибка при обновлении")
    @Test
    public void shouldBeReturnCode400AndInfoWhenUpdatingAnError() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/task");
        Subtask newSubtask = new Subtask(888, "New subtask", "", epic.getId(), Status.NEW);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .header("X-action", "update")
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body().toString()).getAsJsonObject();

        Assertions.assertAll(
                () -> assertEquals(jsonObject.get("status").getAsInt(), 400),
                () -> assertEquals(
                        jsonObject.get("message").getAsString(),
                        "Не получилось обновить задачу c id=888. Проверьте былали создана задача ранее и его " +
                                "совместимость с переданными полями "
                )
        );
    }

    @Test
    public void shouldBeReturnHistory() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/history");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(gson.toJson(taskManager.history()), response.body());
    }

    @Test
    public void shouldBeReturnPrioritizedTasksList() throws IOException, InterruptedException {
        URI uri = URI.create(url + "/tasks/");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.body(), gson.toJson(taskManager.getPrioritizedTasks()));
    }


    @AfterAll
    public static void httpTaskServerTestAfterAll() {
        httpTaskServer.stop();
        kvServer.stop();
    }


}
