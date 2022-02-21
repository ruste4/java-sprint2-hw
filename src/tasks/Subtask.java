package tasks;

import components.Status;

public class Subtask extends Task {
    private int epicID;

    public Subtask(int id, String title, String description, int epicID, Status status) {
        super(id, TaskTypes.SUBTASK, title, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return super.toString() + "," + String.valueOf(epicID);
    }
}
