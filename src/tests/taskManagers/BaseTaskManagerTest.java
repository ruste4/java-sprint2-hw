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
import java.util.Set;
import java.util.TreeSet;

public class BaseTaskManagerTest {
    private static TaskGenerator tg;
    private static BaseTaskManager tm;

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

        Assertions.assertTrue(epicChecklist.containsAll(tm.getAllEpics()));
        Assertions.assertTrue(monotaskCheckList.containsAll(tm.getAllMonotask()));
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

    @Test
    public void getPrioritizedTasks() {
        MonoTask mono1 = new MonoTask(1,"mono1", "", Status.NEW);
        mono1.setStartTime("2022-04-19T10:15:30");

        MonoTask mono2 = new MonoTask(2,"mono2", "", Status.NEW);

        MonoTask mono3 = new MonoTask(3,"mono3", "", Status.NEW);
        mono3.setStartTime("2022-04-15T10:15:30");

        EpicTask epic4 = new EpicTask(4, "epic4", "");

        EpicTask epic5 = new EpicTask(5, "epic5", "");

        EpicTask epic6 = new EpicTask(6, "epic6", "");

        Subtask sub7 = new Subtask(7, "sub7", "", 4, Status.NEW);
        sub7.setStartTime("2022-04-30T10:15:30");
        Subtask sub8 = new Subtask(8, "sub8", "", 4, Status.NEW);
        sub8.setStartTime("2022-04-25T10:15:30");
        Subtask sub9 = new Subtask(9, "sub9", "", 6, Status.NEW);
        sub9.setStartTime("2022-04-21T10:15:30");

        tm.addNewTask(mono1);
        tm.addNewTask(mono2);
        tm.addNewTask(mono3);
        tm.addNewTask(epic4);
        tm.addNewTask(epic5);
        tm.addNewTask(epic6);
        tm.addNewTask(sub7);
        tm.addNewTask(sub8);
        tm.addNewTask(sub9);

        Set<Task> checklist = Set.of(mono3, mono1, epic6, epic4, mono2, epic5);

        Assertions.assertEquals(checklist, tm.getPrioritizedTasks());
    }


    @Test
    // должно быть успешное добавление задачи, если нет пересечения по вермени
    public void shouldBeSuccessfullyAddingMonotaskWithNoTimeIntersection() {
        EpicTask epic1 = new EpicTask(1, "Epic1", "");

        Subtask sub1v1 = new Subtask(2, "Sub1v1", "", 1, Status.NEW);
        sub1v1.setStartTime("2022-04-15T10:15:30");
        sub1v1.setDurationOfMinuts(30);

        Subtask sub2v1 = new Subtask(3, "Sub2v1", "", 1, Status.NEW);
        sub2v1.setStartTime("2022-04-15T12:15:30");
        sub2v1.setDurationOfMinuts(30);

        Subtask sub3v1 = new Subtask(4, "Sub2v1", "", 1, Status.NEW);
        sub3v1.setStartTime("2022-04-15T12:55:30");
        sub3v1.setDurationOfMinuts(15);

        MonoTask mono1 = new MonoTask(5, "Mono1", "", Status.NEW);
        mono1.setStartTime("2022-03-15T12:55:30");
        mono1.setDurationOfMinuts(25);

        MonoTask mono2 = new MonoTask(6, "Mono2", "", Status.NEW);
        mono2.setStartTime("2022-03-15T13:55:30");
        mono2.setDurationOfMinuts(10);

        MonoTask newMonotask = new MonoTask(7, "Mono3", "", Status.NEW);
        newMonotask.setStartTime("2022-03-15T18:55:30");
        newMonotask.setDurationOfMinuts(45);

        tm.addNewTask(epic1);
        tm.addNewTask(sub1v1);
        tm.addNewTask(sub2v1);
        tm.addNewTask(sub3v1);
        tm.addNewTask(mono1);
        tm.addNewTask(mono2);
        tm.addNewTask(newMonotask);

        Assertions.assertNotNull(tm.getTaskById(7));
    }

    @Test
    public void shouldBeSuccessfullyAddingEpicWithNoTimeIntersection() {
        EpicTask epic1 = new EpicTask(1, "Epic1", "");

        Subtask sub1v1 = new Subtask(2, "Sub1v1", "", 1, Status.NEW);
        sub1v1.setStartTime("2022-04-15T10:15:30");
        sub1v1.setDurationOfMinuts(30);

        Subtask sub2v1 = new Subtask(3, "Sub2v1", "", 1, Status.NEW);
        sub2v1.setStartTime("2022-04-15T12:15:30");
        sub2v1.setDurationOfMinuts(30);

        Subtask sub3v1 = new Subtask(4, "Sub2v1", "", 1, Status.NEW);
        sub3v1.setStartTime("2022-04-15T12:55:30");
        sub3v1.setDurationOfMinuts(15);

        MonoTask mono1 = new MonoTask(5, "Mono1", "", Status.NEW);
        mono1.setStartTime("2022-03-15T12:55:30");
        mono1.setDurationOfMinuts(25);

        MonoTask mono2 = new MonoTask(6, "Mono2", "", Status.NEW);
        mono2.setStartTime("2022-03-16T13:55:30");
        mono2.setDurationOfMinuts(10);

        EpicTask newEpic = new EpicTask(7, "newEpic", "");

        Subtask subtask1vN = new Subtask(8, "subtaskForNewEpic", "", 7, Status.NEW);
        subtask1vN.setStartTime("2022-03-15T13:25:30");
        subtask1vN.setDurationOfMinuts(2);
        Subtask subtask2vN = new Subtask(9, "subtaskForNewEpic", "", 7, Status.NEW);
        subtask2vN.setStartTime("2022-03-15T13:30:30");
        subtask2vN.setDurationOfMinuts(5);
        Subtask subtask3vN = new Subtask(10, "subtaskForNewEpic", "", 7, Status.NEW);
        subtask3vN.setStartTime("2022-03-15T13:30:30");
        subtask3vN.setDurationOfMinuts(3);

        newEpic.addSubtask(subtask1vN);
        newEpic.addSubtask(subtask2vN);
        newEpic.addSubtask(subtask3vN);

        tm.addNewTask(epic1);
        tm.addNewTask(sub1v1);
        tm.addNewTask(sub2v1);
        tm.addNewTask(sub3v1);
        tm.addNewTask(mono1);
        tm.addNewTask(mono2);
        tm.addNewTask(newEpic);
        Assertions.assertNotNull(tm.getTaskById(newEpic.getId()));
    }

    @Test
    public void shouldBeSuccessfullyAddingSubtaskWithNoTimeIntersection() {
        EpicTask epic1 = new EpicTask(1, "Epic1", "");

        Subtask sub1v1 = new Subtask(2, "Sub1v1", "", 1, Status.NEW);
        sub1v1.setStartTime("2022-04-15T10:15:30");
        sub1v1.setDurationOfMinuts(30);

        Subtask sub2v1 = new Subtask(3, "Sub2v1", "", 1, Status.NEW);
        sub2v1.setStartTime("2022-04-15T12:15:30");
        sub2v1.setDurationOfMinuts(30);

        Subtask sub3v1 = new Subtask(4, "Sub2v1", "", 1, Status.NEW);
        sub3v1.setStartTime("2022-04-15T12:55:30");
        sub3v1.setDurationOfMinuts(15);

        MonoTask mono1 = new MonoTask(5, "Mono1", "", Status.NEW);
        mono1.setStartTime("2022-03-15T12:55:30");
        mono1.setDurationOfMinuts(25);

        MonoTask mono2 = new MonoTask(6, "Mono2", "", Status.NEW);
        mono2.setStartTime("2022-03-16T13:55:30");
        mono2.setDurationOfMinuts(10);

        tm.addNewTask(epic1);
        tm.addNewTask(sub1v1);
        tm.addNewTask(sub2v1);
        tm.addNewTask(sub3v1);
        tm.addNewTask(mono1);
        tm.addNewTask(mono2);

        Subtask newSubtask = new Subtask(7, "NewSubtask by Epic1", "", 1 ,Status.NEW);
        newSubtask.setStartTime("2022-03-16T13:57:30");
        newSubtask.setDurationOfMinuts(1);

        tm.addNewTask(newSubtask);

        Assertions.assertNotNull(tm.getTaskById(newSubtask.getId()));

    }

    @Test
    public void shouldBeUnsuccessfullyAddingSubtaskWithTimeIntersection() {
        EpicTask epic1 = new EpicTask(1, "Epic1", "");

        Subtask sub1v1 = new Subtask(2, "Sub1v1", "", 1, Status.NEW);
        sub1v1.setStartTime("2022-04-15T10:15:30");
        sub1v1.setDurationOfMinuts(30);

        Subtask sub2v1 = new Subtask(3, "Sub2v1", "", 1, Status.NEW);
        sub2v1.setStartTime("2022-04-15T12:15:30");
        sub2v1.setDurationOfMinuts(30);

        Subtask sub3v1 = new Subtask(4, "Sub2v1", "", 1, Status.NEW);
        sub3v1.setStartTime("2022-04-15T12:55:30");
        sub3v1.setDurationOfMinuts(15);

        MonoTask mono1 = new MonoTask(5, "Mono1", "", Status.NEW);
        mono1.setStartTime("2022-03-15T12:55:30");
        mono1.setDurationOfMinuts(25);

        MonoTask mono2 = new MonoTask(6, "Mono2", "", Status.NEW);
        mono2.setStartTime("2022-03-16T13:55:30");
        mono2.setDurationOfMinuts(10);

        tm.addNewTask(epic1);
        tm.addNewTask(sub1v1);
        tm.addNewTask(sub2v1);
        tm.addNewTask(sub3v1);
        tm.addNewTask(mono1);
        tm.addNewTask(mono2);

        Subtask newSubtask = new Subtask(7, "NewSubtask by Epic1", "", 1 ,Status.NEW);
        newSubtask.setStartTime("2022-03-16T13:57:30");
        newSubtask.setDurationOfMinuts(10);

        tm.addNewTask(newSubtask);

        Assertions.assertNull(tm.getTaskById(newSubtask.getId()));
    }

    @Test
    public void shouldBeUnsuccessfullyAddingMonotaskWithTimeIntersection() {
        EpicTask epic1 = new EpicTask(1, "Epic1", "");

        Subtask sub1v1 = new Subtask(2, "Sub1v1", "", 1, Status.NEW);
        sub1v1.setStartTime("2022-04-15T10:15:30");
        sub1v1.setDurationOfMinuts(30);

        Subtask sub2v1 = new Subtask(3, "Sub2v1", "", 1, Status.NEW);
        sub2v1.setStartTime("2022-04-15T12:15:30");
        sub2v1.setDurationOfMinuts(30);

        Subtask sub3v1 = new Subtask(4, "Sub2v1", "", 1, Status.NEW);
        sub3v1.setStartTime("2022-04-15T12:55:30");
        sub3v1.setDurationOfMinuts(15);

        MonoTask mono1 = new MonoTask(5, "Mono1", "", Status.NEW);
        mono1.setStartTime("2022-03-15T12:55:30");
        mono1.setDurationOfMinuts(25);

        MonoTask mono2 = new MonoTask(6, "Mono2", "", Status.NEW);
        mono2.setStartTime("2022-03-15T13:55:30");
        mono2.setDurationOfMinuts(10);

        MonoTask newMonotask = new MonoTask(7, "Mono3", "", Status.NEW);
        newMonotask.setStartTime("2022-03-15T13:55:30");
        newMonotask.setDurationOfMinuts(45);

        tm.addNewTask(epic1);
        tm.addNewTask(sub1v1);
        tm.addNewTask(sub2v1);
        tm.addNewTask(sub3v1);
        tm.addNewTask(mono1);
        tm.addNewTask(mono2);
        tm.addNewTask(newMonotask);

        Assertions.assertNull(tm.getTaskById(7));
    }
}
