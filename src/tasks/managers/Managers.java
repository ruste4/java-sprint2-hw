package tasks.managers;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }
}
