package manager;

import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, MonoTask> monoTasks;

    public Manager() {
        this.epicTasks = new HashMap<>();
        this.monoTasks = new HashMap<>();
    }

    /**
     * Метод вернет true, если задача была успешно добавлена.
     * Если в хранилище найдется задача с таким же индексом как у новой задачи,
     * метод вернет false и добавления не будет.
     *
     * @param task
     * @return boolean
     */

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

    /**
     * Метод вернет false: если искомый id не был найден,
     * если тип переданной задачи не совпадает с найденной по id задачей
     *
     * @param task
     * @return boolean
     */
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

    public ArrayList<MonoTask> getAllMonotask() {
        ArrayList<MonoTask> result = new ArrayList<>();

        result.addAll(monoTasks.values());
        return result;
    }

    public ArrayList<EpicTask> getAllEpics() {
        ArrayList<EpicTask> result = new ArrayList<>();

        result.addAll(epicTasks.values());
        return result;
    }

    /**
     * Вернет nell, если задачи с переданным id нет в хранилище
     *
     * @param epicId
     * @return
     */
    public ArrayList<Subtask> getSubtasksDefinedEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        Task foundTask = getTaskById(epicId);
        if (!(foundTask instanceof EpicTask)) return null;
        if (foundTask == null) return null;
        HashMap<Integer, Subtask> epicSubtasks = ((EpicTask) foundTask).getSubtasks();
        result.addAll(epicSubtasks.values());
        return result;
    }


    /**
     * Метод вернет null, если задачи с переданным id нет
     *
     * @param id
     * @return Task
     */
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

    /**
     * Метод вернет true, если задача была ранее добавлена
     *
     * @param id
     * @return
     */
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

    public void removeAllTasks() {
        epicTasks.clear();
        monoTasks.clear();
    }

}
