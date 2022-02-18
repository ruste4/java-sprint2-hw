package tasksmanagers;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }
}
