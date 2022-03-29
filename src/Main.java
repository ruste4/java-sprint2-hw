import generators.TaskGenerator;
import server.HttpTaskServer;
import server.KVServer;
import server.KVTaskClient;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;

import java.io.IOException;

public class Main {
    private static HttpTaskServer server = new HttpTaskServer();

    public static void main(String[] args) throws IOException {
        String json = "{\n" +
                "\t\"epicID\": 2,\n" +
                "\t\"title\": \"Sub Task2\",\n" +
                "\t\"description\": \"Description sub task3\",\n" +
                "\t\"id\": 7,\n" +
                "\t\"status\": \"DONE\",\n" +
                "\t\"type\": \"SUBTASK\",\n" +
                "\t\"duration\": 0\n" +
                "}";
        KVServer kvServer = new KVServer();
        kvServer.start();
        KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8078/");
        kvTaskClient.put("id7", json);
        System.out.println(kvTaskClient.load("id7"));
    }
}
