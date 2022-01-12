package manager;

import components.History;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTasksManager implements TaskManager {
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
        if (task == null) return false;
        int taskId = task.getID();

        if (getTaskById(taskId) != null) return false;

        if (task instanceof Subtask) {
            int epicID = ((Subtask) task).getEpicID();

            EpicTask epicTask = (EpicTask) getTaskById(epicID);
            if (epicTask == null) return false;
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
        if (task == null) return false;
        Task foundTaskById = getTaskById(id);
        if (foundTaskById == null || foundTaskById.getClass() != task.getClass()) return false;
        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicID();

            EpicTask epic = (EpicTask) getTaskById(epicId);
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

        Task foundTask = getTaskById(epicId);
        if (!(foundTask instanceof EpicTask)) return null;
        if (foundTask == null) return null;
        HashMap<Integer, Subtask> epicSubtasks = ((EpicTask) foundTask).getSubtasks();
        result.addAll(epicSubtasks.values());
        return result;
    }

    @Override
    public Task getTaskById(int id) {
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
    public boolean removeTaskById(int id) {
        Task task = getTaskById(id);
        if (task == null) return false;
        if (task instanceof EpicTask) {
            epicTasks.remove(id);
            return true;
        } else if (task instanceof MonoTask) {
            monoTasks.remove(id);
            return true;
        } else if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getEpicID();
            EpicTask epicTask = (EpicTask) getTaskById(epicId);
            if (epicTask == null) return false;
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
        Task subtask = getTaskById(id);
        if (subtask == null || !(subtask instanceof Subtask)) return null;
        history.addTaskIdToTheHistory(subtask);
        return (Subtask) subtask;
    }

    @Override
    public EpicTask getEpic(int id) {
        Task epicTask = getTaskById(id);
        if (epicTask == null || !(epicTask instanceof EpicTask)) return null;
        history.addTaskIdToTheHistory(epicTask);
        return (EpicTask) epicTask;
    }

    @Override
    public ArrayList<Task> history() {
        return history.getHistory();
    }

}
