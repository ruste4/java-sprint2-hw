package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.endpoints.MonotaskHandler;
import com.sun.net.httpserver.HttpServer;
import server.typeAdapters.DurationAdapter;
import server.typeAdapters.LocalDateTimeAdapter;
import tasksmanagers.Managers;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private TaskManager taskManager;
    private Gson gson;

    public HttpTaskServer() {
        this.taskManager = Managers.getFileBackedTasksManager();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public void start() throws IOException { //todo подумай над обработкой ошибки внутри метода
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks/task", new MonotaskHandler(taskManager, gson));
        httpServer.start();
    }
}
