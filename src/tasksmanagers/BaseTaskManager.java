package tasksmanagers;

import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import tasks.*;

import java.util.*;

public class BaseTaskManager implements TaskManager {
    private TreeMap<Integer, EpicTask> epicTasks;
    private TreeMap<Integer, MonoTask> monoTasks;
    private HistoryManager historyManager;

    public BaseTaskManager() {
        this.epicTasks = new TreeMap<>();
        this.monoTasks = new TreeMap<>();
        this.historyManager = new InMemoryHistoryManager();
    }

    protected void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

//    public TreeSet getPrioritizedTasks() {
//
//    }

    @Override
    public boolean addNewTask(Task task) {
        if (task == null) {
            return false;
        }
        int taskId = task.getId();

        if (findTaskById(taskId) != null) {
            return false;
        }

        if (task.getType() == TaskTypes.SUBTASK) {
            int epicID = ((Subtask) task).getEpicID();

            EpicTask epicTask = (EpicTask) findTaskById(epicID);
            if (epicTask == null) {
                return false;
            }
            boolean addSubtaskResult = epicTask.addSubtask((Subtask) task);
            return addSubtaskResult;
        } else if (task.getType() == TaskTypes.EPIC) {
            epicTasks.put(taskId, (EpicTask) task);
            return true;
        } else if (task.getType() == TaskTypes.MONOTASK) {
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
        if (task.getType() == TaskTypes.SUBTASK) {
            int epicId = ((Subtask) task).getEpicID();

            EpicTask epic = (EpicTask) findTaskById(epicId);
            epic.updateSubtask((Subtask) task);
            return true;
        } else if (task.getType() == TaskTypes.MONOTASK) {
            monoTasks.put(id, (MonoTask) task);
            return true;
        } else if (task.getType() == TaskTypes.EPIC) {
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
        if (foundTask == null || !(foundTask.getType() == TaskTypes.EPIC)) {
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
        historyManager.add(task);
        return task;
    }

    @Override
    public boolean removeTaskById(int id) {
        Task task = findTaskById(id);
        if (task == null) {
            return false;
        }
        if (task.getType() == TaskTypes.EPIC) {
            HashMap<Integer, Subtask> subtasks = ((EpicTask) task).getSubtasks();
            for (int subtaskId : subtasks.keySet()) {
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            epicTasks.remove(id);
            return true;
        } else if (task.getType() == TaskTypes.MONOTASK) {
            historyManager.remove(id);
            monoTasks.remove(id);
            return true;
        } else if (task.getType() == TaskTypes.SUBTASK) {
            int epicId = ((Subtask) task).getEpicID();
            EpicTask epicTask = (EpicTask) findTaskById(epicId);
            if (epicTask == null) {
                return false;
            }
            historyManager.remove(id);
            epicTask.removeSubtask(id);
            return true;
        }
        return false;
    }

    @Override
    public void removeAllTasks() {
        epicTasks.clear();
        monoTasks.clear();
        historyManager.clearHistory();
    }

    @Override
    public Subtask getSubtask(int id) {
        Task subtask = findTaskById(id);
        if (subtask == null || !(subtask.getType() == TaskTypes.SUBTASK)) {
            return null;
        }
        historyManager.add(subtask);
        return (Subtask) subtask;
    }

    @Override
    public EpicTask getEpic(int id) {
        Task epicTask = findTaskById(id);
        if (epicTask == null || !(epicTask.getType() == TaskTypes.EPIC)) {
            return null;
        }
        historyManager.add(epicTask);
        return (EpicTask) epicTask;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}
