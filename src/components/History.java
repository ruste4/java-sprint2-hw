package components;

import tasks.Task;

import java.util.ArrayList;

public class History {
    private int HISTORY_CAPACITY = 10;
    private ArrayList<Task> history;

    public History() {
        this.history = new ArrayList<>();
    }

    public void addTaskIdToTheHistory(Task task) {
        if (history.size() >= HISTORY_CAPACITY) {
            history.remove(0);
        }
        history.add(task);
    }

    public ArrayList<Task> getHistory() {
        return history;
    }

}
