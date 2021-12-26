package tasks;

import components.Status;

import java.util.HashMap;

public class EpicTask extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public EpicTask(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    private void updateStatus() {
        if (subtasks.isEmpty() || subtasks.size() == countTasksByStatus(Status.NEW)) {
            status = Status.NEW;
        } else if (subtasks.size() == countTasksByStatus(Status.DONE)) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    private int countTasksByStatus(Status status) {
        int result = 0;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus() == status) {
                result++;
            }
        }
        return result;
    }

    public Subtask searchSubtaskById(int id) {
        return subtasks.get(id);
    }

    /**
     * Метод вернет false, в хранилище уже есть задача с таким же id как у переданной задачи.
     *
     * @param subtask
     * @return boolean
     */
    public boolean addSubtask(Subtask subtask) {
        if (subtask == null) return false;
        int subtaskId = subtask.getID();
        if (subtasks.containsKey(subtaskId)) return false;
        subtasks.put(subtaskId, subtask);
        updateStatus();
        return true;
    }

    /**
     * Метод вернет false, в хранилище нет задачи с таким же id как у переданной задачи.
     *
     * @param subtask
     * @return boolean
     */
    public boolean updateSubtask(Subtask subtask) {
        if (subtask == null) return false;
        int subtaskId = subtask.getID();
        if (!subtasks.containsKey(subtaskId)) return false;
        subtasks.put(subtaskId, subtask);
        updateStatus();
        return true;
    }

    /**
     * Метод вернет false, если в хранилище нет subtask с переданным id
     *
     * @param subtaskId
     * @return boolean
     */
    public boolean removeSubtask(int subtaskId) {
        if (!(subtasks.containsKey(subtaskId))) return false;
        subtasks.remove(subtaskId);
        updateStatus();
        return true;
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        updateStatus();
    }

}
