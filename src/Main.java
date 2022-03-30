import components.Status;
import server.HttpTaskServer;
import server.KVServer;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasksmanagers.HTTPTaskManager;

import java.io.IOException;

public class Main {
    private static HttpTaskServer server = new HttpTaskServer();

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();

        HTTPTaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8078/");

        EpicTask epic1 = new EpicTask(1, "Epic1", "");
        Subtask sub1 = new Subtask(2, "Subtask for Epic1", "", 1 ,Status.NEW);
        Subtask sub2 = new Subtask(3, "Subtask for Epic1", "", 1 ,Status.NEW);
        MonoTask mono1 = new MonoTask(4, "Monotask1", "", Status.NEW);
        MonoTask mono2 = new MonoTask(5, "Monotask2", "", Status.NEW);

        sub1.setStartTime("2022-03-13T12:15:30");
        sub2.setStartTime("2022-03-18T12:15:30");
        mono1.setStartTime("2022-01-13T12:15:30");
        mono2.setStartTime("2022-02-13T12:15:30");

        httpTaskManager.addNewTask(epic1);

        httpTaskManager.addNewTask(sub1);
        httpTaskManager.addNewTask(sub2);
        httpTaskManager.addNewTask(mono1);
        httpTaskManager.addNewTask(mono2);

        HTTPTaskManager httpTaskManagerNEW = new HTTPTaskManager("http://localhost:8078/");
        httpTaskManagerNEW.loadFromVkServer("DEBUG");
        System.out.println(1);


    }
}
