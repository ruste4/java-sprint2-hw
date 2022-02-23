package tests.taskManagers;

import components.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;
import tasksmanagers.FileBackedTasksManager;

import java.io.File;

class FileBackedTasksManagerTest {
    @Test
    public void saveAndLoadFromFile() {
        String testFilePath = "src/tests/taskManagers/taskSaveTest.csv";
        File file = new File(testFilePath);
        FileBackedTasksManager managerForSave = new FileBackedTasksManager(file);
        Task monotask = new MonoTask(1, "Task1", "Description", Status.NEW);
        Task epic = new EpicTask(2, "Epic2", "Description");
        Task subtask = new Subtask(3, "Sub Task2", "Description sub task3", 2, Status.DONE);

        managerForSave.addNewTask(monotask);
        managerForSave.addNewTask(epic);
        managerForSave.addNewTask(subtask);
        managerForSave.getTaskById(2);
        managerForSave.getTaskById(1);

        FileBackedTasksManager managerForLoadTasks = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertEquals(managerForSave.getAllEpics(), managerForLoadTasks.getAllEpics());
        Assertions.assertEquals(managerForSave.getAllMonotask(), managerForLoadTasks.getAllMonotask());
        Assertions.assertEquals(managerForSave.history(), managerForLoadTasks.history());
    }
}