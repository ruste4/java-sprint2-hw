package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasksmanagers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HistoryHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = handleGetHistoryRequest();
        handlerResponse(httpExchange, response, 200);
    }

    private String handleGetHistoryRequest() {
        return gson.toJson(taskManager.history());
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
