package tasksmanagers;

import components.FileReader;
import components.FileWriter;
import components.Status;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {
    private final File saveFile;

    public FileBackedTasksManager() {
        this.saveFile = new File("src/tasksaves/taskSave.csv");
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
     *
     * @param file
     * @return Возвращает FileBackedTasksManager с загруженными из файла задачами и историей
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try {
            List<String> lines = FileReader.readeFromFile(file);
            if (lines.isEmpty()) {
                return null;
            }

            for (int i = 1; i < lines.size() - 1; i++) {
                if (lines.get(i).isBlank()) {
                    continue;
                }
                String[] column = lines.get(i).split(",");
                int id = Integer.parseInt(column[0]);
                String title = column[2];
                Status status = Status.valueOf(column[3]);
                String description = column[4];

                if (column[1].equals(TaskTypes.MONOTASK.name())) {
                    fileBackedTasksManager.addNewTask(new MonoTask(id, title, description, status));
                } else if (column[1].equals(TaskTypes.EPIC.name())) {
                    fileBackedTasksManager.addNewTask(new EpicTask(id, title, description));
                } else if (column[1].equals(TaskTypes.SUBTASK.name())) {
                    int epicId = Integer.parseInt(column[5]);
                    fileBackedTasksManager.addNewTask(new Subtask(id, title, description, epicId, status));
                }
            }

            String historyLine = lines.get(lines.size() - 1);
            String[] historyIds = historyLine.split(",");
            for (String taskId : historyIds) {
                fileBackedTasksManager.getTaskById(Integer.parseInt(taskId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileBackedTasksManager;
    }

    public static void main(String[] args) {
        Task monotask = new MonoTask(1, "Task1", "Description", Status.NEW);
        Task epic = new EpicTask(2, "Epic2", "Description");
        Task subtask = new Subtask(3, "Sub Task2", "Description sub task3", 2, Status.DONE);

        TaskManager managers1 = Managers.getFileBackedTasksManager();
        managers1.addNewTask(monotask);
        managers1.addNewTask(epic);
        managers1.addNewTask(subtask);
        managers1.getTaskById(2);
        managers1.getTaskById(1);

        File file = new File("src/tasksaves/taskSave.csv");
        TaskManager manager2 = FileBackedTasksManager.loadFromFile(file);

        if (!managers1.getAllEpics().equals(manager2.getAllEpics())) {
            System.out.println("!!! Возвращаемые менеджерами списки Epics не совпадают");
        }
        if (!managers1.getAllMonotask().equals(manager2.getAllMonotask())) {
            System.out.println("!!! Возвращаемые менеджерами списки MotoTask не совпадают");
        }
        if (!managers1.history().equals(manager2.history())) {
            System.out.println("!!! Истории менеджеров не совпадают");
        }
    }

}
