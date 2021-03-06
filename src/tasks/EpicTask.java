package tasks;

import components.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class EpicTask extends Task {
    private HashMap<Integer, Subtask> subtasks;

    public EpicTask(int id, String title, String description) {
        super(id, TaskTypes.EPIC, title, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Status getStatus() {
        if (subtasks.isEmpty() || subtasks.size() == countTasksByStatus(Status.NEW)) {
            return Status.NEW;
        } else if (subtasks.size() == countTasksByStatus(Status.DONE)) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    @Override
    public Duration getDuration() {
        if (getStartTime() == null) {
            return Duration.ZERO;
        }
        LocalDateTime lastSubtaskFinish = LocalDateTime.MIN;
        for (Subtask subtask : getSubtasks().values()) {
            if (subtask.getStartTime() == null) {
                break;
            }
            if (subtask.getFinishTime().isAfter(lastSubtaskFinish)) {
                lastSubtaskFinish = subtask.getFinishTime();
            }
        }
        return Duration.between(getStartTime(), lastSubtaskFinish);
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime startFirstSubtask = LocalDateTime.MAX;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() != null &&
                    startFirstSubtask.isAfter(subtask.getStartTime())) {
                startFirstSubtask = subtask.getStartTime();
            }
        }
        if (startFirstSubtask.isEqual(LocalDateTime.MAX)) {
            return null;
        } else {
            return startFirstSubtask;
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
     * ?????????? ???????????? false, ?? ?????????????????? ?????? ???????? ???????????? ?? ?????????? ???? id ?????? ?? ???????????????????? ????????????.
     *
     * @param subtask
     * @return boolean
     */
    public boolean addSubtask(Subtask subtask) {
        if (subtask == null) return false;
        int subtaskId = subtask.getId();
        if (subtasks.containsKey(subtaskId)) return false;
        subtasks.put(subtaskId, subtask);
        return true;
    }

    /**
     * ?????????? ???????????? false, ?? ?????????????????? ?????? ???????????? ?? ?????????? ???? id ?????? ?? ???????????????????? ????????????.
     *
     * @param subtask
     * @return boolean
     */
    public boolean updateSubtask(Subtask subtask) {
        if (subtask == null) return false;
        int subtaskId = subtask.getId();
        if (!subtasks.containsKey(subtaskId)) return false;
        subtasks.put(subtaskId, subtask);
        return true;
    }

    /**
     * ?????????? ???????????? false, ???????? ?? ?????????????????? ?????? subtask ?? ???????????????????? id
     *
     * @param subtaskId
     * @return boolean
     */
    public boolean removeSubtask(int subtaskId) {
        if (!(subtasks.containsKey(subtaskId))) return false;
        subtasks.remove(subtaskId);
        return true;
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

}
