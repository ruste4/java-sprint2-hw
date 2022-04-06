package tasksmanagers;

import com.google.gson.*;
import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import server.KVTaskClient;
import server.exceptions.RequestException;
import server.exceptions.TaskException;
import server.typeAdapters.DurationAdapter;
import server.typeAdapters.ExceptionAdapter;
import server.typeAdapters.LocalDateTimeAdapter;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HTTPTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    private Gson gson;

    public HTTPTaskManager(String url) {
        this.kvTaskClient = new KVTaskClient(url);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskException.class, new ExceptionAdapter())
                .registerTypeAdapter(RequestException.class, new ExceptionAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void save() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(getAllMonotask());
        for (EpicTask epicTask : getAllEpics()) {
            tasks.add(epicTask);
        }
        String tasksJson = gson.toJson(tasks);
        String historyJson = gson.toJson(history());

        kvTaskClient.put("tasks", tasksJson);
        kvTaskClient.put("history", historyJson);
    }

    public void loadFromVkServer(String kvTaskClientKeyApi) {
        kvTaskClient.setKeyApi(kvTaskClientKeyApi);

        String tasksJson = kvTaskClient.load("tasks");
        String historyJson = kvTaskClient.load("history");

        ArrayList<Task> tasks = parseTasksJsonToTasksList(tasksJson);
        ArrayList<Task> history = parseTasksJsonToTasksList(historyJson);

        HistoryManager historyManager = new InMemoryHistoryManager();

        tasks.forEach(super::loadNewTask);
        history.forEach(historyManager::add);

        setHistoryManager(historyManager);

    }

    private ArrayList<Task> parseTasksJsonToTasksList(String json) {
        ArrayList<Task> result = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (JsonElement element : jsonArray) {

            String type = element.getAsJsonObject().get("type").getAsString();

            switch (type) {
                case "MONOTASK":
                    result.add(gson.fromJson(element, MonoTask.class));
                    break;
                case "SUBTASK":
                    result.add(gson.fromJson(element, Subtask.class));
                    break;
                case "EPIC":
                    result.add(gson.fromJson(element, EpicTask.class));
                    break;
            }
        }

        return result;
    }
}
