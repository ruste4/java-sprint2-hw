package manager;

import history.History;
import history.HistoryManager;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTasksManager implements TaskManager, HistoryManager {
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, MonoTask> monoTasks;

    private History history;

    public InMemoryTasksManager() {
        this.epicTasks = new HashMap<>();
        this.monoTasks = new HashMap<>();
        this.history = new History();
    }

    @Override
    public boolean addNewTask(Task task) {
        if (task == null) {
            return false;
        }
        int taskId = task.getId();

        if (findTaskById(taskId) != null) {
            return false;
        }

        if (task instanceof Subtask) {
            int epicID = ((Subtask) task).getEpicID();

            EpicTask epicTask = (EpicTask) findTaskById(epicID);
            if (epicTask == null) {
                return false;
            }
            boolean addSubtaskResult = epicTask.addSubtask((Subtask) task);
            return addSubtaskResult;
        } else if (task instanceof EpicTask) {
            epicTasks.put(taskId, (EpicTask) task);
            return true;
        } else if (task instanceof MonoTask) {
            monoTasks.put(taskId, (MonoTask) task);
            return true;
        }

        return false;
    }

    @Override
    public boolean updateTask(Task task, int id) {
        if (task == null) {
            return false;
        }
        Task foundTaskById = findTaskById(id);
        if (foundTaskById == null || foundTaskById.getClass() != task.getClass()) {
            return false;
        }
        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicID();

            EpicTask epic = (EpicTask) findTaskById(epicId);
            epic.updateSubtask((Subtask) task);
            return true;
        } else if (task instanceof MonoTask) {
            monoTasks.put(id, (MonoTask) task);
            return true;
        } else if (task instanceof EpicTask) {
            epicTasks.put(id, (EpicTask) task);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<MonoTask> getAllMonotask() {
        ArrayList<MonoTask> result = new ArrayList<>();

        result.addAll(monoTasks.values());
        return result;
    }

    @Override
    public ArrayList<EpicTask> getAllEpics() {
        ArrayList<EpicTask> result = new ArrayList<>();

        result.addAll(epicTasks.values());
        return result;
    }

    @Override
    public ArrayList<Subtask> getSubtasksDefinedEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        Task foundTask = findTaskById(epicId);
        if (foundTask == null || !(foundTask instanceof EpicTask)) {
            return null;
        }
        HashMap<Integer, Subtask> epicSubtasks = ((EpicTask) foundTask).getSubtasks();
        result.addAll(epicSubtasks.values());
        return result;
    }

    private Task findTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        }
        if (monoTasks.containsKey(id)) {
            return monoTasks.get(id);
        }
        for (EpicTask epicTask : epicTasks.values()) {
            Subtask requiredSubtask = epicTask.searchSubtaskById(id);
            if (requiredSubtask != null) {
                return requiredSubtask;
            }
        }
        return null;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = findTaskById(id);
        if (task == null) {
            return null;
        }
        addToHistory(task);
        return task;
    }

    @Override
    public boolean removeTaskById(int id) {
        Task task = findTaskById(id);
        if (task == null) {
            return false;
        }
        if (task instanceof EpicTask) {
            epicTasks.remove(id);
            return true;
        } else if (task instanceof MonoTask) {
            monoTasks.remove(id);
            return true;
        } else if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicID();
            EpicTask epicTask = (EpicTask) findTaskById(epicId);
            if (epicTask == null) {
                return false;
            }
            epicTask.removeSubtask(id);
            return true;
        }
        return false;
    }

    @Override
    public void removeAllTasks() {
        epicTasks.clear();
        monoTasks.clear();
    }

    @Override
    public Subtask getSubtask(int id) {
        Task subtask = findTaskById(id);
        if (subtask == null || !(subtask instanceof Subtask)) {
            return null;
        }
        addToHistory(subtask);
        return (Subtask) subtask;
    }

    @Override
    public EpicTask getEpic(int id) {
        Task epicTask = findTaskById(id);
        if (epicTask == null || !(epicTask instanceof EpicTask)) {
            return null;
        }
        addToHistory(epicTask);
        return (EpicTask) epicTask;
    }

    @Override
    public void addToHistory(Task task) {
        history.linkLast(task);
    }

    @Override
    public void removeFromHistory(int id) {
        history.removeNodeById(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}
