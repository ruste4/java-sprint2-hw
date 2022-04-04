package tests.taskManagers;

import com.google.gson.*;
import components.Status;
import org.junit.jupiter.api.*;
import server.KVServer;
import server.KVTaskClient;
import server.exceptions.RequestException;
import server.exceptions.TaskException;
import server.typeAdapters.DurationAdapter;
import server.typeAdapters.ExceptionAdapter;
import server.typeAdapters.LocalDateTimeAdapter;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasksmanagers.HTTPTaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest extends FileBackedTasksManagerTest {
    static KVServer kvServer;
    HTTPTaskManager httpTaskManager;
    static KVTaskClient kvTaskClient;
    static Gson gson;

    @BeforeAll
    public static void beforeAllHTTPTaskManagerTests() {
        try {
            kvServer = new KVServer();
            kvServer.start();

            gson = new GsonBuilder()
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(TaskException.class, new ExceptionAdapter())
                    .registerTypeAdapter(RequestException.class, new ExceptionAdapter())
                    .setPrettyPrinting()
                    .create();

            kvTaskClient = new KVTaskClient("http://localhost:8078/");
            kvTaskClient.setKeyApi("DEBUG");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @BeforeEach
    public void beforeEachHTTPTaskManagerTests() {
        httpTaskManager = new HTTPTaskManager("http://localhost:8078/");
    }

    @Test
    public void shouldBeSaveEpicWithoutSubtasks() {
        EpicTask epic = new EpicTask(1, "Epic without subtasks", "");
        httpTaskManager.addNewTask(epic);
        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        EpicTask epicFromKVServer = gson.fromJson(jsonArray.get(0), EpicTask.class);

        assertEquals(epic, epicFromKVServer);
    }

    @Test
    public void shouldBeSaveEpicWithSubtasks() {
        EpicTask epic = new EpicTask(1, "Epic with subtasks", "");
        Subtask subtask1 = new Subtask(2, "Subtask1 for Epic1", "", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask2 for Epic1", "", 1, Status.NEW);
        httpTaskManager.addNewTask(epic);
        httpTaskManager.addNewTask(subtask1);
        httpTaskManager.addNewTask(subtask2);
        String responseFromKVServer = kvTaskClient.load("tasks");

        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        EpicTask epicFromKVServer = gson.fromJson(jsonArray.get(0), EpicTask.class);

        Assertions.assertAll(
                () -> assertEquals(epic, epicFromKVServer),
                () -> Assertions.assertTrue(epicFromKVServer.getSubtasks().containsValue(subtask1)),
                () -> Assertions.assertTrue(epicFromKVServer.getSubtasks().containsValue(subtask2))
        );
    }

    @Test
    public void shouldBeSaveMonotasks() {
        MonoTask mono1 = new MonoTask(1, "Monotask1", "", Status.NEW);
        MonoTask mono2 = new MonoTask(2, "Monotask2", "", Status.NEW);
        httpTaskManager.addNewTask(mono1);
        httpTaskManager.addNewTask(mono2);

        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        MonoTask mono1FromKVServer = gson.fromJson(jsonArray.get(0), MonoTask.class);
        MonoTask mono2FromKVServer = gson.fromJson(jsonArray.get(1), MonoTask.class);

        Assertions.assertAll(
                () -> assertEquals(mono1, mono1FromKVServer),
                () -> assertEquals(mono2, mono2FromKVServer)
        );
    }

    @Test
    public void shouldBeUpdateMonotaskInKVServerByUpdatedMonotaskInTaskManager() {
        MonoTask mono = new MonoTask(1, "Title before update", "", Status.NEW);
        httpTaskManager.addNewTask(mono);
        String newTitle = "new title for mono";
        mono.setTitle(newTitle);
        httpTaskManager.updateTask(mono, mono.getId());

        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        MonoTask monoFromKVServer = gson.fromJson(jsonArray.get(0), MonoTask.class);

        assertEquals(monoFromKVServer.getTitle(), newTitle);
    }

    @Test
    public void shouldBeUpdateEpicInKVServerByUpdatedEpicInTaskManager() {
        EpicTask epic = new EpicTask(1, "Epic1", "");
        httpTaskManager.addNewTask(epic);
        String newEpicTitle = "New epic title";
        epic.setTitle(newEpicTitle);
        httpTaskManager.updateTask(epic, epic.getId());

        String responseFromKVServer = kvTaskClient.load("tasks");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        EpicTask epicFromKVServer = gson.fromJson(jsonArray.get(0), EpicTask.class);

        assertEquals(epicFromKVServer.getTitle(), newEpicTitle);

    }

    @Test
    public void shouldBeSaveHistoryOnKVserver() {
        EpicTask epic = new EpicTask(1, "Epic1", "");
        Subtask subtask1 = new Subtask(2, "Subtask1 for Epic1", "", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask2 for Epic1", "", 1, Status.NEW);
        MonoTask mono = new MonoTask(4, "Mono", "", Status.NEW);

        httpTaskManager.addNewTask(epic);
        httpTaskManager.addNewTask(subtask1);
        httpTaskManager.addNewTask(subtask2);
        httpTaskManager.addNewTask(mono);

        httpTaskManager.getTaskById(2);
        httpTaskManager.getTaskById(4);
        httpTaskManager.getTaskById(1);

        String responseFromKVServer = kvTaskClient.load("history");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        Subtask subtask1FromJson = gson.fromJson(jsonArray.get(0), Subtask.class);
        MonoTask monotaskFromJson = gson.fromJson(jsonArray.get(1), MonoTask.class);
        EpicTask epicFromJson = gson.fromJson(jsonArray.get(2), EpicTask.class);

        Assertions.assertAll(
                () -> assertEquals(subtask1, subtask1FromJson),
                () -> assertEquals(mono, monotaskFromJson),
                () -> assertEquals(epic, epicFromJson)
        );
    }

    @Test
    public void shouldBeUpdateHistoryOnKVserverByGetPreviouslyAddedTask() {
        EpicTask epic = new EpicTask(1, "Epic1", "");
        Subtask subtask1 = new Subtask(2, "Subtask1 for Epic1", "", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask2 for Epic1", "", 1, Status.NEW);
        MonoTask mono = new MonoTask(4, "Mono", "", Status.NEW);

        httpTaskManager.addNewTask(epic);
        httpTaskManager.addNewTask(subtask1);
        httpTaskManager.addNewTask(subtask2);
        httpTaskManager.addNewTask(mono);

        httpTaskManager.getTaskById(2);
        httpTaskManager.getTaskById(4);
        httpTaskManager.getTaskById(1);
        httpTaskManager.getTaskById(2);

        String responseFromKVServer = kvTaskClient.load("history");
        JsonArray jsonArray = JsonParser.parseString(responseFromKVServer).getAsJsonArray();
        MonoTask monotaskFromJson = gson.fromJson(jsonArray.get(0), MonoTask.class);
        EpicTask epicFromJson = gson.fromJson(jsonArray.get(1), EpicTask.class);
        Subtask subtask1FromJson = gson.fromJson(jsonArray.get(2), Subtask.class);

        Assertions.assertAll(
                () -> assertEquals(subtask1, subtask1FromJson),
                () -> assertEquals(mono, monotaskFromJson),
                () -> assertEquals(epic, epicFromJson)
        );
    }

    @Test
    public void shouldBeLoadeTaskManagerStateFromKVserver() {
        EpicTask epic = new EpicTask(1, "Epic1", "");
        Subtask subtask1 = new Subtask(2, "Subtask1 for Epic1", "", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask2 for Epic1", "", 1, Status.NEW);
        MonoTask mono = new MonoTask(4, "Mono", "", Status.NEW);

        httpTaskManager.addNewTask(epic);
        httpTaskManager.addNewTask(subtask1);
        httpTaskManager.addNewTask(subtask2);
        httpTaskManager.addNewTask(mono);

        HTTPTaskManager loadedTaskManager = new HTTPTaskManager("http://localhost:8078/");
        loadedTaskManager.loadFromVkServer("DEBUG");

        Assertions.assertAll(
                () -> assertEquals(httpTaskManager.getAllEpics(), loadedTaskManager.getAllEpics()),
                () -> assertEquals(httpTaskManager.getAllMonotask(), loadedTaskManager.getAllMonotask()),
                () -> assertEquals(httpTaskManager.getSubtasksDefinedEpic(epic.getId()),
                        loadedTaskManager.getSubtasksDefinedEpic(epic.getId())),
                () -> assertEquals(httpTaskManager.history(), loadedTaskManager.history())
        );
    }

    @AfterAll
    public static void afterAllHTTPTaskManagerTests() {
        kvServer.stop();
    }
}
