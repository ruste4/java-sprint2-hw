package tasksmanagers;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTasksManager();
    }
    public static TaskManager getFileBackedTasksManager() {
        //todo Исправь на нормальный вид, согласно ТЗ
        String testFilePath = "src/tasksmanagers/taskSaveTest.csv";
        File file = new File(testFilePath);
        return FileBackedTasksManager.loadFromFile(file);
    }
}
