package tests.taskManagers;

import components.Status;
import generators.TaskGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;
import tasksmanagers.BaseTaskManager;
import tasksmanagers.TaskManager;

import java.util.List;

public class TaskManagerTest {
    private static TaskGenerator tg;
    private static TaskManager tm;

    @BeforeEach
    public void beforeEach() {
        tg = new TaskGenerator();
        tm = new BaseTaskManager();
    }

    //должно добавить эпик, подзадачу и задачу
    @Test
    public void shouldBeAddEpicSubtaskAndMonotask() {
        EpicTask epic = tg.generateEpicTask();
        EpicTask epic2 = tg.generateEpicTask();
        Subtask subtask = tg.generateSubtask(epic.getId());
        MonoTask monoTask = tg.generateMonotask();
        MonoTask monoTask2 = tg.generateMonotask();

        tm.addNewTask(epic);
        tm.addNewTask(epic2);
        tm.addNewTask(subtask);
        tm.addNewTask(monoTask);
        tm.addNewTask(monoTask2);

        epic.addSubtask(subtask);

        List<Task> epicChecklist = List.of(epic, epic2);
        List<Task> monotaskCheckList = List.of(monoTask, monoTask2);

        Assertions.assertTrue(epicChecklist.equals(tm.getAllEpics()));
        Assertions.assertTrue(monotaskCheckList.equals(tm.getAllMonotask()));
    }

    @Test
    public void updateTaskTest() {
        // обновляем EpicTask
        int epicId = 1;
        EpicTask epic = new EpicTask(epicId, "Title", "Description");
        tm.addNewTask(epic);
        EpicTask updateEpicTask = new EpicTask(epicId, "Updated epic", "Description");
        tm.updateTask(updateEpicTask, epicId);
        Assertions.assertEquals(tm.getEpic(epicId).getTitle(),(updateEpicTask.getTitle()));

        //обновление Monotask
        int monotaskId = 2;
        MonoTask monoTask = new MonoTask(monotaskId, "Monotask title", "Descripton", Status.NEW);
        tm.addNewTask(monoTask);
        MonoTask updatedMonotask = new MonoTask(monotaskId, "Updated monotask", "Description", Status.NEW);
        tm.updateTask(updatedMonotask, monotaskId);
        Assertions.assertEquals(updatedMonotask.getTitle(), tm.getTaskById(2).getTitle());

        //обновление Subtask
        int subtaskId = 3;
        Subtask subtask = new Subtask(subtaskId, "Subtask title", "Description", epicId, Status.NEW);
        tm.addNewTask(subtask);
        Subtask updSubtask = new Subtask(subtaskId, "Upd subtask title", "upd", epicId, Status.NEW);
        tm.updateTask(updSubtask, subtaskId);
        Assertions.assertEquals(updSubtask.getTitle(), tm.getTaskById(subtaskId).getTitle());
        Assertions.assertFalse(tm.updateTask(updSubtask, 111));
    }

    @Test
    public void shuldBeReturn3monotasksAnd3EpicTasks() {
        MonoTask monoTask1 = tg.generateMonotask();
        MonoTask monoTask2 = tg.generateMonotask();
        MonoTask monoTask3 = tg.generateMonotask();
        EpicTask epic1 = tg.generateEpicTask();
        EpicTask epic2 = tg.generateEpicTask();
        EpicTask epic3 = tg.generateEpicTask();

        tm.addNewTask(monoTask1);
        tm.addNewTask(monoTask2);
        tm.addNewTask(monoTask3);
        tm.addNewTask(epic1);
        tm.addNewTask(epic2);
        tm.addNewTask(epic3);

        List<Task> monotasks = List.of(monoTask1,monoTask2, monoTask3);
        List<Task> epics = List.of(epic1, epic2, epic3);

        // Тестируем методы getAllMonotask, getAllEpics
        Assertions.assertEquals(monotasks, tm.getAllMonotask());
        Assertions.assertEquals(epics, tm.getAllEpics());
    }

    @Test
    public void getSubtasksDefinedEpicTest() {
        EpicTask epic = tg.generateEpicTask();
        int epicId = epic.getId();
        Subtask subtask1 = tg.generateSubtask(epicId);
        Subtask subtask2 = tg.generateSubtask(epicId);
        Subtask subtask3 = tg.generateSubtask(epicId);

        tm.addNewTask(epic);
        tm.addNewTask(subtask1);
        tm.addNewTask(subtask2);
        tm.addNewTask(subtask3);

        List<Task> subtaskCheckList = List.of(subtask1, subtask2, subtask3);
        Assertions.assertTrue(tm.getSubtasksDefinedEpic(epicId).containsAll(subtaskCheckList));
        Assertions.assertNull(tm.getSubtasksDefinedEpic(111));
    }

    @Test
    public void getTaskByIdTest() {
        Task task = tg.generateMonotask();
        int taskId = task.getId();
        tm.addNewTask(task);
        Assertions.assertEquals(task, tm.getTaskById(taskId));
        Assertions.assertNull(tm.getTaskById(111));
    }

    @Test
    public void removeTaskByIdTest() {
        EpicTask epic = tg.generateEpicTask();
        Subtask subTask = tg.generateSubtask(epic.getId());
        MonoTask monotask = tg.generateMonotask();

        tm.addNewTask(epic);
        tm.addNewTask(subTask);
        tm.addNewTask(monotask);

        tm.removeTaskById(subTask.getId());
        tm.removeTaskById(monotask.getId());

        Assertions.assertNull(tm.getTaskById(subTask.getId()));
        Assertions.assertNull(tm.getTaskById(monotask.getId()));
        Assertions.assertFalse(tm.removeTaskById(111));
    }

    @Test
    public void removeAllTasksTest() {
        EpicTask epic = tg.generateEpicTask();
        Subtask subTask = tg.generateSubtask(epic.getId());
        MonoTask monotask = tg.generateMonotask();

        tm.addNewTask(epic);
        tm.addNewTask(subTask);
        tm.addNewTask(monotask);

        tm.removeAllTasks();

        Assertions.assertTrue(tm.getAllMonotask().isEmpty());
        Assertions.assertTrue(tm.getAllEpics().isEmpty());
    }

    @Test
    public void getSubtaskTest() {
        EpicTask epic = tg.generateEpicTask();
        Subtask subTask = tg.generateSubtask(epic.getId());

        tm.addNewTask(epic);
        tm.addNewTask(subTask);

        Assertions.assertNotNull(tm.getSubtask(subTask.getId()));
        Assertions.assertNull((tm.getSubtask(2)));
    }

    @Test
    public void getEpicTest() {
        EpicTask epic1 = tg.generateEpicTask();
        EpicTask epic2 = tg.generateEpicTask();

        tm.addNewTask(epic1);
        tm.addNewTask(epic2);

        Assertions.assertNotNull(tm.getEpic(epic1.getId()));
        Assertions.assertNull(tm.getEpic(10));
    }

    @Test
    public void historyTest() {
        MonoTask monoTask1 = tg.generateMonotask();
        MonoTask monoTask2 = tg.generateMonotask();
        MonoTask monoTask3 = tg.generateMonotask();
        EpicTask epic1 = tg.generateEpicTask();
        EpicTask epic2 = tg.generateEpicTask();
        EpicTask epic3 = tg.generateEpicTask();
        Subtask subtask1 = tg.generateSubtask(epic1.getId());
        Subtask subtask2 = tg.generateSubtask(epic1.getId());

        tm.addNewTask(monoTask1);
        tm.addNewTask(monoTask2);
        tm.addNewTask(monoTask3);
        tm.addNewTask(epic1);
        tm.addNewTask(epic2);
        tm.addNewTask(epic3);
        tm.addNewTask(subtask1);
        tm.addNewTask(subtask2);

        List<Task> checklist = List.of(monoTask1, subtask2, epic3);
        tm.getTaskById(monoTask1.getId());
        tm.getSubtask(subtask2.getId());
        tm.getEpic(epic3.getId());
        Assertions.assertEquals(checklist, tm.history());

        //Проверяем на изменнение истории при повторном вызове задачи
        List<Task> checklistAfterChangeHistory = List.of(subtask2, epic3, monoTask1);
        tm.getTaskById(monoTask1.getId());
        Assertions.assertEquals(checklistAfterChangeHistory, tm.history());
    }
}
