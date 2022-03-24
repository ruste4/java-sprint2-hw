package server.endpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.helpers.QueryParamGetter;
import tasks.Task;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.Gson;

public class MonotaskHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public MonotaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        switch (method) {
            case "GET":
                response = getMonotask.apply(exchange);
                break;
            case "POST":
                exchange.sendResponseHeaders(200, 0);
                response = "тут я создаю новую задачу"; //todo незабудь исправить
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                response = "Метод " + method + " не поддреживается";
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }

    }

    private Function<HttpExchange, String> getMonotask = exchange -> {
        String response = "";
        URI url = exchange.getRequestURI();
        Optional<Integer> id = QueryParamGetter.getTaskId(url);
        Task task;

        try {
            if (id.isPresent()) {
                task = taskManager.getTaskById(id.get());

                if (task != null) {
                    exchange.sendResponseHeaders(200, 0);
                    response = gson.toJson(task);
                } else {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Задача с таким иденификатором не была найдена";
                }
            } else {
                exchange.sendResponseHeaders(404, 0);
                response = "Параметр id не был найден";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    };
}
