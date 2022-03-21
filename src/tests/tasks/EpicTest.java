package tests.tasks;

import components.Status;
import generators.TaskGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;

import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTest {
    private EpicTask epic;
    private TaskGenerator taskGenerator = new TaskGenerator();

    @BeforeEach
    public void beforeEach() {
        epic = new EpicTask(1, "Epic for test", "description");
    }

    @Test
    public void emptyTaskList() {
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void shouldBeDurationZeroIfAllSubtaskWithoutDuration() {
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId()));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId()));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId()));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId()));

        Assertions.assertTrue(epic.getDuration().isZero());
    }

    @Test
    public void shouldBeDuration120minuts() {
        Subtask subtask1 = taskGenerator.generateSubtask(epic.getId());
        subtask1.setStartTime("2022-03-13T10:15:30");
        subtask1.setDurationOfMinuts(60);

        Subtask subtask2 = taskGenerator.generateSubtask(epic.getId());
        subtask2.setStartTime("2022-03-13T12:15:30");
        subtask2.setDurationOfMinuts(30);

        Duration testDuration = Duration.between(LocalDateTime.parse("2022-03-13T10:15:30"),
                LocalDateTime.parse("2022-03-13T12:45:30"));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Duration see = epic.getDuration();
        Assertions.assertTrue(testDuration.equals(epic.getDuration()));
    }

    @Test
    public void shouldBeStarTimeMostPrioritySubtask() {
        LocalDateTime dateTime = LocalDateTime.parse("2022-03-13T10:15:30");
        Subtask subtask1 = taskGenerator.generateSubtask(epic.getId());
        Subtask subtask2 = taskGenerator.generateSubtask(epic.getId());

        subtask1.setStartTime("2022-04-13T10:15:30");
        subtask2.setStartTime("2022-03-13T10:15:30");

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertTrue(dateTime.isEqual(epic.getStartTime()));
    }


    @Test
    public void shouldBeStatusNewByAllSubtasksStatusNew() {
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));

        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeStatusDoneByAllSubtasksStatusDone() {
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressBySubtasksStatusDoneAndNew() {
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.NEW));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.DONE));

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeStatusInProgressByAllSubtasksStatusInProgress() {
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.IN_PROGRESS));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.IN_PROGRESS));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.IN_PROGRESS));
        epic.addSubtask(taskGenerator.generateSubtask(epic.getId(), Status.IN_PROGRESS));

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeFinishTime1HoursMoreByAdd2SubtaskDuration30minuts() {
        EpicTask epic = taskGenerator.generateEpicTask();

        Subtask sub1 = taskGenerator.generateSubtask(epic.getId());
        sub1.setStartTime("2022-04-13T10:15:30");
        sub1.setDurationOfMinuts(30);

        Subtask sub2 = taskGenerator.generateSubtask(epic.getId());
        sub2.setStartTime("2022-04-13T10:45:30");
        sub2.setDurationOfMinuts(30);

        epic.addSubtask(sub1);
        epic.addSubtask(sub2);

        LocalDateTime ctrlFinishTime = LocalDateTime.parse("2022-04-13T11:15:30");

        Assertions.assertTrue(epic.getFinishTime().equals(ctrlFinishTime));
    }

}
