package generators;

import components.Status;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;

public class TaskGenerator {
    public EpicTask generateEpicTask() {
        int id = IDGenerator.getID();
        String title = "Title for EpicTask with ID: " + id;
        String description = "Description for EpicTask with ID: " + id;

        return new EpicTask(id, title, description);
    }

    public Subtask generateSubtask(int epicTaskID) {
        int id = IDGenerator.getID();
        String title = "Title for Subtask with ID: " + id;
        String description = "Description for Subtask with ID: " + id;

        return new Subtask(id, title, description, epicTaskID, Status.NEW);
    }

    public Subtask generateSubtask(int epicTaskID, Status status) {
        int id = IDGenerator.getID();
        String title = "Title for Subtask with ID: " + id;
        String description = "Description for Subtask with ID: " + id;

        return new Subtask(id, title, description, epicTaskID, status);
    }

    public MonoTask generateMonotask() {
        int id = IDGenerator.getID();
        String title = "Title for Monotask with ID: " + id;
        String description = "Description for Monotask with ID: " + id;

        return new MonoTask(id, title, description, Status.NEW);
    }

}
