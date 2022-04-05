import components.Status;
import server.HttpTaskServer;
import server.KVServer;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasksmanagers.HTTPTaskManager;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = new HTTPTaskManager("http://localhost:8078/");
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        EpicTask epic = new EpicTask(1, "Epic", "");
        Subtask subtask1 = new Subtask(2, "Subtask1", "", 1, Status.NEW);
        Subtask subtask2 = new Subtask(3, "Subtask2", "", 1, Status.NEW);
        MonoTask monoTask1 = new MonoTask(4, "Monotask1", "", Status.NEW);
        MonoTask monoTask2 = new MonoTask(5, "Monotask2", "", Status.NEW);

        subtask1.setStartTime("2022-03-13T12:15:30");
        subtask2.setStartTime("2022-01-13T12:15:30");
        monoTask1.setStartTime("2022-04-13T12:15:30");
        monoTask2.setStartTime("2022-05-13T12:15:30");

        httpTaskServer.start();

        taskManager.addNewTask(epic);
        taskManager.addNewTask(subtask1);
        taskManager.addNewTask(subtask2);
        taskManager.addNewTask(monoTask1);
        taskManager.addNewTask(monoTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse response = client.send(req, HttpResponse.BodyHandlers.ofString());


    }
}
