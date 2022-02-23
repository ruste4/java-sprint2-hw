package tasks;

import components.Status;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private final int id;
    protected Status status;
    private TaskTypes type;

    public Task(int ID, TaskTypes type, String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.id = ID;
        this.status = status;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setType(TaskTypes type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        String[] statusArray = {String.valueOf(id), String.valueOf(type),
                title, String.valueOf(getStatus()), description};
        return String.join(",", statusArray);
    }
}
