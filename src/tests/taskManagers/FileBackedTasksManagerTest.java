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

class FileBackedTasksManagerTest  extends BaseTaskManagerTest {
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
        Assertions.assertAll(
                () -> Assertions.assertEquals(managerForSave.getAllEpics(), managerForLoadTasks.getAllEpics()),
                () -> Assertions.assertEquals(managerForSave.getAllMonotask(), managerForLoadTasks.getAllMonotask()),
                () -> Assertions.assertEquals(managerForSave.history(), managerForLoadTasks.history())
        );
    }

    @Test
    public void saveAndLoadFromFileByEmptyTaskListAndByEmptyHistory() {
        String testFilePath = "src/tests/taskManagers/taskSaveTestByEmptyTaskList.csv";
        File file = new File(testFilePath);
        Task monotask = new MonoTask(1, "Task1", "Description", Status.NEW);

        FileBackedTasksManager managerForSave = new FileBackedTasksManager(file);
        managerForSave.addNewTask(monotask);
        managerForSave.removeTaskById(monotask.getId());

        FileBackedTasksManager managerForLoadTasks = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertTrue(managerForLoadTasks.getAllEpics().isEmpty());
    }

    @Test
    public void saveAndLoadFromFileByEpicWithoutSubtasks() {
        String testFilePath = "src/tests/taskManagers/taskSaveTestByEpicWithoutSubtasks.csv";
        File file = new File(testFilePath);
        FileBackedTasksManager managerForSave = new FileBackedTasksManager(file);
        Task epic1 = new EpicTask(2, "Epic2", "Description");
        Task epic2= new EpicTask(3, "Epic3", "Description");

        managerForSave.addNewTask(epic1);
        managerForSave.addNewTask(epic2);

        FileBackedTasksManager managerForLoadTasks = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertEquals(managerForSave.getAllEpics(), managerForLoadTasks.getAllEpics());
    }
}