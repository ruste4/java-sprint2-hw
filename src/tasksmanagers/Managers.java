package tasksmanagers;

public class Managers {
    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078/");
    }

}
