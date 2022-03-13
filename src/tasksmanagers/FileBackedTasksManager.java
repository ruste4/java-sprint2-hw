package tasksmanagers;

import components.FileWriter;
import components.Status;
import exceptions.ManagerSaveException;
import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends BaseTaskManager {
    private static final String DEFAULT_SAVE_FILE_NAME = "src/tasksaves/taskSaveDefault.csv";
    private final File saveFile;

    public FileBackedTasksManager() {
        this.saveFile = new File(DEFAULT_SAVE_FILE_NAME);
    }

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;

    }

    @Override
    public boolean addNewTask(Task task) {
        boolean result = super.addNewTask(task);
        save();
        return result;
    }

    private boolean loadNewTask(Task task) {
        return super.addNewTask(task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public boolean removeTaskById(int id) {
        boolean result = super.removeTaskById(id);
        save();
        return result;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public EpicTask getEpic(int id) {
        EpicTask epicTask = super.getEpic(id);
        save();
        return epicTask;
    }


    /**
     * Сохранить
     * Метод перезаписывает текущее состояние FileBackedTasksManager в файл.
     */
    private void save() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(getAllMonotask());
        for (EpicTask epicTask : getAllEpics()) {
            tasks.add(epicTask);
            tasks.addAll(epicTask.getSubtasks().values());
        }
        try {
            FileWriter.writeTasksToCSV(tasks, saveFile, history());
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    /**
     * Загрузить из файла
     * Если загрузочного файла не существует вернет объект FileBackedTasksManager без задач.
     * Этот объект будет записывать свои сохранения в файл указанный в DEFAULT_SAVE_FILE_NAME.
     *
     * @param file
     * @return Возвращает FileBackedTasksManager с загруженными из файла задачами и историей
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        if (!file.exists()) {
            System.out.println("------------------------------------------------------");
            System.out.println("Файл загрузки не был найден, создаю пустой TaskManager");
            System.out.println("------------------------------------------------------");
            return new FileBackedTasksManager();
        }

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            HashMap<Integer, Task> loadedTasks = new HashMap<>();
            HistoryManager historyManager = new InMemoryHistoryManager();
            String historyLine = lines.get(lines.size() - 1);
            String[] historyIds = historyLine.split(",");

            for (int i = 1; i < lines.size() - 1; i++) {
                if (lines.get(i).isBlank()) {
                    continue;
                }
                String[] column = lines.get(i).split(",");
                int id = Integer.parseInt(column[0]);
                String title = column[2];
                Status status = Status.valueOf(column[3]);
                String description = column[4];
                String duration = column[5];
                String startTime = column[6];
                Task task;
                if (column[1].equals(TaskTypes.MONOTASK.name())) {
                    task = new MonoTask(id, title, description, status);
                } else if (column[1].equals(TaskTypes.EPIC.name())) {
                    task = new EpicTask(id, title, description);
                } else {
                    int epicId = Integer.parseInt(column[7]);
                    task = new Subtask(id, title, description, epicId, status);
                }
                task.setDurationOfMinuts(Long.parseLong(duration));
                task.setStartTime(startTime);
                fileBackedTasksManager.loadNewTask(task);
                loadedTasks.put(task.getId(), task);
            }

            for (String taskId : historyIds) {
                if (!taskId.isBlank()) {
                    historyManager.add(loadedTasks.get(Integer.parseInt(taskId)));
                }
            }
            fileBackedTasksManager.setHistoryManager(historyManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileBackedTasksManager;
    }
}
