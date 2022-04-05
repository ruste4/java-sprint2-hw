package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import server.exceptions.IllegalHeaderException;
import server.handlers.HistoryHandler;
import server.handlers.PrioritizedTasksHandler;
import server.handlers.TaskHandler;
import server.exceptions.RequestException;
import server.exceptions.TaskException;
import server.typeAdapters.DurationAdapter;
import server.typeAdapters.ExceptionAdapter;
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
    HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskException.class, new ExceptionAdapter())
                .registerTypeAdapter(RequestException.class, new ExceptionAdapter())
                .registerTypeAdapter(IllegalHeaderException.class, new ExceptionAdapter())
                .setPrettyPrinting()
                .create();

        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/tasks/", new PrioritizedTasksHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}
