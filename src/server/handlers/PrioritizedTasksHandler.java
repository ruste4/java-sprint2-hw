package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.exceptions.RequestException;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PrioritizedTasksHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String response = "";
        int statusCode = 200;

        try {
            String method = httpExchange.getRequestMethod();

            switch (method) {
                case "GET":
                    response = handleGetPrioritizedTasksRequest();
                    break;
                default:
                    throw new RequestException("Метод " + method + " не поддерживается");
            }

        } catch (RequestException e) {
            e.printStackTrace();
            statusCode = 400;
            response = gson.toJson(e);
        } finally {
            handlerResponse(httpExchange, response, statusCode);
        }

    }

    private String handleGetPrioritizedTasksRequest() {
        return gson.toJson(taskManager.getPrioritizedTasks());
    }

    private void handlerResponse(HttpExchange httpExchange, String response, int statusCode) {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(statusCode, 0);
            os.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
