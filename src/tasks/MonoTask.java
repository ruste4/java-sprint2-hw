package tasks;

import components.Status;

public class MonoTask extends Task{
    public MonoTask(int id, String title, String description, Status status) {
        super(id, TaskTypes.MONOTASK, title, description, status);
    }
}
