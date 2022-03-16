package tasksmanagers;

import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import tasks.*;

import java.util.*;

public class BaseTaskManager implements TaskManager {
    private TreeSet<Task> tasks;
    private HistoryManager historyManager;

    public BaseTaskManager() {
        this.tasks = new TreeSet<>();
        this.historyManager = new InMemoryHistoryManager();
    }

    protected void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return tasks;
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

        if (task.getType() == TaskTypes.SUBTASK) {
            int epicID = ((Subtask) task).getEpicID();

            EpicTask epicTask = (EpicTask) findTaskById(epicID);
            if (epicTask == null) {
                return false;
            }
            tasks.remove(epicTask);
            boolean result = epicTask.addSubtask((Subtask) task);
            tasks.add(epicTask);
            return result;
        } else {
            return tasks.add(task);
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
        tasks.remove(foundTaskById);
        if (task.getType() == TaskTypes.SUBTASK) {
            int epicId = ((Subtask) task).getEpicID();

            EpicTask epic = (EpicTask) findTaskById(epicId);
            epic.updateSubtask((Subtask) task);
            return true;
        } else {
            tasks.add(task);
            return true;
        }
    }

    @Override
    public ArrayList<MonoTask> getAllMonotask() {
        ArrayList<MonoTask> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskTypes.MONOTASK) {
                result.add((MonoTask) task);
            }
        }
        return result;
    }

    @Override
    public ArrayList<EpicTask> getAllEpics() {
        ArrayList<EpicTask> result = new ArrayList<>();
        for (Task task : tasks) {
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
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
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
            tasks.remove(task);
            return true;
        } else if (task.getType() == TaskTypes.MONOTASK) {
            historyManager.remove(id);
            tasks.remove(task);
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
