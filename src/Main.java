import generators.TaskGenerator;
import server.HttpTaskServer;
import server.KVServer;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;

import java.io.IOException;

public class Main {
    private static HttpTaskServer server = new HttpTaskServer();

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
    }
}
