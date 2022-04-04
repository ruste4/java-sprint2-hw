package tasksmanagers;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078/");
    }

}
