package server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.exceptions.*;
import server.helpers.QueryParamGetter;
import tasks.*;
import tasksmanagers.TaskManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.OptionalInt;

public class TaskHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
                    response = handleGetTaskRequest(httpExchange);
                    break;
                case "POST":
                    handlePostTaskRequest(httpExchange);
                    break;
                case "Delete":
                    handleDeleteTaskRequest(httpExchange);
                    break;
                default:
                    throw new RequestException("Метод " + method + " не поддерживается");
            }

        } catch (IllegalHeaderException | RequestException | TaskException e) {
            e.printStackTrace();
            System.out.println(e.toString());
            statusCode = 400;
            response = gson.toJson(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handlerResponse(httpExchange, response, statusCode);
        }


    }

    private void handlerResponse(HttpExchange httpExchange, String response, int statusCode) {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(statusCode, 0);
            os.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Спарсить json в задачу
     *
     * @param body
     * @return
     * @throws RequestException выбросится, если содержимое тела не поддерживает синтаксис json
     *                          или если в переменной "type" указан неправельный тип задачи
     */
    private Task parseJsonToTask(String body) throws RequestException {
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            throw new RequestException("Содерживое тела не является json обектом");
        }

        String type = jsonElement.getAsJsonObject().get("type").getAsString();

        switch (type) {
            case "MONOTASK":
                return gson.fromJson(body, MonoTask.class);
            case "SUBTASK":
                return gson.fromJson(body, Subtask.class);
            case "EPIC":
                return gson.fromJson(body, EpicTask.class);
            default:
                throw new RequestException("Unchecked case " + type);
        }
    }

    /**
     * Обработать GET запроса задач
     *
     * @param httpExchange
     * @return Вернет сериализованный объект задачи в формате json
     * @throws TaskException    Будет выброшен если задачи с переданным id нет
     * @throws RequestException Будет выброшен если в параметрах запроса не был найден параметр "id"
     */
    private String handleGetTaskRequest(HttpExchange httpExchange) throws TaskException, RequestException {
        String response = "";
        URI uri = httpExchange.getRequestURI();
        String query = httpExchange.getRequestURI().getQuery();
        OptionalInt id;

        if (query == null) {
            return gson.toJson(taskManager.getAllMonotask());
        }

        id = QueryParamGetter.getIdValueFromQuery(uri);
        if (id.isPresent()) {
            Task task = taskManager.getTaskById(id.getAsInt());
            if (task == null) {
                throw new TaskException("Задача c id=" + id.getAsInt() + " не найдена");
            }
            response = gson.toJson(task);
        } else {
            throw new RequestException("Параметр id в строке запроса не был найден");
        }

        return response;
    }


    /**
     * Обработать POST запрос для задач
     *
     * @param httpExchange
     * @throws IllegalHeaderException если нет заголовка X-action, со значениями "create" или "update"
     * @throws IOException            генерирует InputStream.readAllBytes()
     * @throws RequestException       генерирует parseJsonToTask()
     * @throws TaskException          если не получилось добавить новую задачу или обновить старую
     */
    private void handlePostTaskRequest(HttpExchange httpExchange) throws IllegalHeaderException, IOException,
            RequestException, TaskException {

        List<String> action = httpExchange.getRequestHeaders().get("X-action");
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = parseJsonToTask(body);

        if (action == null) {
            throw new IllegalHeaderException("Нет заголовка 'X-action'");
        }

        if (action.contains("create")) {
            boolean isAdded = taskManager.addNewTask(task);
            if (!isAdded) {
                throw new TaskException("Не получилось добавить новую задачу с id=" + task.getId() +
                        ", возможно, она была добавлена ранее");
            }
        } else if (action.contains("update")) {
            boolean isUpdated = taskManager.updateTask(task, task.getId());
            if (!isUpdated) {
                throw new TaskException("Не получилось обновить задачу c id=" + task.getId() +
                        ". Проверьте былали создана задача ранее и его совместимость с переданными полями ");
            }
        }
    }

    /**
     * Обработать DELETE запрос для задач
     *
     * @param httpExchange
     * @throws TaskException если, задачи с переданным id нет в репозитории
     */
    private void handleDeleteTaskRequest(HttpExchange httpExchange) throws TaskException {
        URI uri = httpExchange.getRequestURI();
        OptionalInt id = QueryParamGetter.getIdValueFromQuery(uri);

        if (id.isPresent()) {
            boolean isDeleted = taskManager.removeTaskById(id.getAsInt());
            if (!isDeleted) {
                throw new TaskException("Задача с id=" + id.getAsInt() + " не найдена");
            }
        } else {
            taskManager.removeAllTasks();
        }
    }
}
