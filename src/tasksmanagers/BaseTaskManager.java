package tasksmanagers;

import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import tasks.*;

import java.util.*;

public class BaseTaskManager implements TaskManager {
    private TreeMap<Integer, Task> tasks;
    private HistoryManager historyManager;

    public BaseTaskManager() {
        this.tasks = new TreeMap<>();
        this.historyManager = new InMemoryHistoryManager();
    }

    protected void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

//    public TreeSet getPrioritizedTasks() {
//        return tasks.t();
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
        } else {
            tasks.put(taskId, task);
            return true;
        }
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
        } else {
            tasks.put(id, task);
            return true;
        }
    }

    @Override
    public ArrayList<MonoTask> getAllMonotask() {
        ArrayList<MonoTask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getType() == TaskTypes.MONOTASK) {
                result.add((MonoTask) task);
            }
        }
        return result;
    }

    @Override
    public ArrayList<EpicTask> getAllEpics() {
        ArrayList<EpicTask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getType() == TaskTypes.EPIC) {
                result.add((EpicTask) task);
            }
        }
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
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        for (Task task : tasks.values()) {
            if (task.getType() == TaskTypes.EPIC) {
                Subtask requiredSubtask = ((EpicTask) task).searchSubtaskById(id);
                if (requiredSubtask != null) {
                    return requiredSubtask;
                }
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
            tasks.remove(id);
            return true;
        } else if (task.getType() == TaskTypes.MONOTASK) {
            historyManager.remove(id);
            tasks.remove(id);
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
        tasks.clear();
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
