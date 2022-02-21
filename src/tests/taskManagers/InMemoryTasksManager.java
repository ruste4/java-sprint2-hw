package tests.taskManagers;

import components.Status;
import generators.TaskGenerator;
import tasksmanagers.Managers;
import tasksmanagers.TaskManager;
import org.junit.jupiter.api.Assertions;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

class InMemoryTasksManager {
    TaskGenerator taskGenerator = new TaskGenerator();

    @org.junit.jupiter.api.Test
    void addNewTask() {
        TaskManager manager = Managers.getDefault();
        EpicTask epicTask1 = taskGenerator.generateEpicTask();
        EpicTask epicTask2 = taskGenerator.generateEpicTask();
        EpicTask epicTask3 = taskGenerator.generateEpicTask();
        Subtask subtask1 = taskGenerator.generateSubtask(epicTask1.getId());

        ArrayList<EpicTask> tasksList = new ArrayList<>();
        tasksList.add(epicTask1);
        tasksList.add(epicTask2);
        tasksList.add(epicTask3);

        // Добавляем EpicTask-и
        manager.addNewTask(epicTask1);
        manager.addNewTask(epicTask2);
        manager.addNewTask(epicTask3);

        Assertions.assertEquals(tasksList, manager.getAllEpics());
        Assertions.assertFalse(manager.addNewTask(new EpicTask(epicTask1.getId(), "Epic", "Epic")));

        // Проверяем добавление Subtask
        Assertions.assertTrue(manager.addNewTask(subtask1));
        EpicTask epicConteinerForSubtask = (EpicTask) manager.getTaskById(subtask1.getEpicID());
        Assertions.assertTrue(epicConteinerForSubtask.getSubtasks().containsValue(subtask1));
        Subtask subtask2 = new Subtask(1, "Subtask", "Subtask", 2, Status.NEW);
        Assertions.assertFalse(manager.addNewTask(subtask2));
    }

    @org.junit.jupiter.api.Test
    void updateTask() {
        TaskManager manager = Managers.getDefault();
        EpicTask epicTask = new EpicTask(11, "Test update Task", "Test");
        Subtask subtask = new Subtask(12, "Test update Task", "Test update ", 11, Status.NEW);

        manager.addNewTask(epicTask);
        manager.addNewTask(subtask);

        Subtask updatedSubtask = new Subtask(12, "Updated title", "Test update ", 11, Status.NEW);
        Assertions.assertTrue(manager.updateTask(updatedSubtask, 12));
        Assertions.assertTrue(manager.getTaskById(12).getTitle().equals(updatedSubtask.getTitle()));
        Assertions.assertFalse(manager.updateTask(updatedSubtask, 123));

        EpicTask updateEpicTask = new EpicTask(11, "Updated Task", "Test");
        Assertions.assertTrue(manager.updateTask(updateEpicTask, 11));
        Assertions.assertTrue(manager.getTaskById(11).getTitle().equals(updateEpicTask.getTitle()));
        Assertions.assertFalse(manager.updateTask(updateEpicTask, 123));
    }

    @org.junit.jupiter.api.Test
    void getSubtasksDefinedEpic() {
        TaskManager manager = Managers.getDefault();
        int epicTaskID = 111;

        ArrayList<Subtask> subtaskList = new ArrayList<>();
        EpicTask epicTask = new EpicTask(epicTaskID, "EpicTask title", "EpicTask description");
        manager.addNewTask(epicTask);

        Subtask subtask1 = taskGenerator.generateSubtask(epicTaskID);
        subtaskList.add(subtask1);
        manager.addNewTask(subtask1);

        Subtask subtask2 = taskGenerator.generateSubtask(epicTaskID);
        subtaskList.add(subtask2);
        manager.addNewTask(subtask2);

        Subtask subtask3 = taskGenerator.generateSubtask(epicTaskID);
        subtaskList.add(subtask3);
        manager.addNewTask(subtask3);

        Assertions.assertEquals(manager.getSubtasksDefinedEpic(epicTaskID), subtaskList);
    }

    @org.junit.jupiter.api.Test
    void getTaskById() {
        TaskManager manager = Managers.getDefault();
        MonoTask monoTask = taskGenerator.generateMonotask();
        int monoTaskID = monoTask.getId();
        manager.addNewTask(monoTask);
        Task methodResult = manager.getTaskById(monoTaskID);
        Assertions.assertEquals(monoTask, methodResult);
    }

    @org.junit.jupiter.api.Test
    void removeTaskById() {
        TaskManager manager = Managers.getDefault();
        MonoTask monoTask = taskGenerator.generateMonotask();
        int taskID = monoTask.getId();
        manager.addNewTask(monoTask);
        Assertions.assertTrue(manager.removeTaskById(taskID));
        Assertions.assertFalse(manager.removeTaskById(taskID));
    }

    @org.junit.jupiter.api.Test
    void removeAllTasks() {
        TaskManager manager = Managers.getDefault();
        MonoTask monoTask = taskGenerator.generateMonotask();
        EpicTask epicTask = taskGenerator.generateEpicTask();
        Subtask subtask = taskGenerator.generateSubtask(epicTask.getId());
        manager.addNewTask(monoTask);
        manager.addNewTask(epicTask);
        manager.addNewTask(subtask);
        manager.removeAllTasks();
        Assertions.assertTrue(manager.getAllEpics().isEmpty());
        Assertions.assertTrue(manager.getAllMonotask().isEmpty());
    }

    @org.junit.jupiter.api.Test
    void getSubtask() {
        TaskManager manager = Managers.getDefault();
        EpicTask epicTask = taskGenerator.generateEpicTask();
        Subtask subtask = taskGenerator.generateSubtask(epicTask.getId());
        int subtaskID = subtask.getId();
        manager.addNewTask(epicTask);
        manager.addNewTask(subtask);
        Assertions.assertEquals(manager.getSubtask(subtaskID), subtask);
    }

    @org.junit.jupiter.api.Test
    void getEpic() {
        TaskManager manager = Managers.getDefault();
        EpicTask epicTask = taskGenerator.generateEpicTask();
        int epicTaskID = epicTask.getId();
        manager.addNewTask(epicTask);
        Assertions.assertEquals(manager.getEpic(epicTaskID), epicTask);
    }

    @org.junit.jupiter.api.Test
    void history() {
        TaskManager manager = Managers.getDefault();
        ArrayList<Task> controlArr = new ArrayList<>();

        Task epic1 = taskGenerator.generateEpicTask();
        Task epic2 = taskGenerator.generateEpicTask();
        Task subtask11 = taskGenerator.generateSubtask(epic1.getId());
        Task subtask12 = taskGenerator.generateSubtask(epic1.getId());
        Task subtask13 = taskGenerator.generateSubtask(epic1.getId());
        Task monotask1 = taskGenerator.generateMonotask();

        manager.addNewTask(epic1);
        manager.addNewTask(epic2);
        manager.addNewTask(subtask11);
        manager.addNewTask(subtask12);
        manager.addNewTask(subtask13);
        manager.addNewTask(monotask1);

        manager.getTaskById(epic1.getId());
        controlArr.add(epic1);

        manager.getSubtask(subtask11.getId());
        controlArr.add(subtask11);

        manager.getEpic(epic2.getId());
        controlArr.add(epic2);

        manager.getTaskById(subtask13.getId());
        controlArr.add(subtask13);

        manager.getTaskById(monotask1.getId());
        controlArr.add(monotask1);

        // Повторно вызваем задачу
        manager.getEpic(epic1.getId());
        controlArr.remove(epic1);
        controlArr.add(epic1);

        Assertions.assertEquals(controlArr, manager.history());

        //Проверка удаления задачи
        manager.removeTaskById(epic2.getId());
        controlArr.remove(epic2);

        Assertions.assertEquals(controlArr, manager.history());

        // Проверка на удаление эпика вместе с его подзадачами
        manager.removeTaskById(epic1.getId());
        controlArr.remove(epic1);
        controlArr.remove(subtask11);
        controlArr.remove(subtask12);
        controlArr.remove(subtask13);

        Assertions.assertEquals(controlArr, manager.history());
    }
}