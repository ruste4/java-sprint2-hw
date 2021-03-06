package tests.historyManagers;

import generators.TaskGenerator;
import historymanagers.HistoryManager;
import historymanagers.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    private static TaskGenerator taskGenerator;
    private static Task monotask;
    private static Task epic;
    private static Task epic1;
    private static Task subtask;
    private static Task subtask1;

    @BeforeAll
    public static void beforeAll() {
        taskGenerator = new TaskGenerator();
        monotask = taskGenerator.generateMonotask();
        epic = taskGenerator.generateEpicTask();
        epic1 = taskGenerator.generateEpicTask();
        subtask = taskGenerator.generateSubtask(epic.getId());
        subtask1 = taskGenerator.generateSubtask(epic.getId());
    }

    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        ArrayList<Task> controlArr = new ArrayList<>();

        historyManager.add(monotask);
        controlArr.add(monotask);

        historyManager.add(epic);
        controlArr.add(epic);

        historyManager.add(epic1);
        controlArr.add(epic1);

        historyManager.add(subtask);
        controlArr.add(subtask);

        historyManager.add(subtask1);
        controlArr.add(subtask1);

        Assertions.assertEquals(controlArr, historyManager.getHistory());

        // Добавляем задачу, которая ранее была добавлена в историю
        historyManager.add(monotask);
        controlArr.remove(monotask);
        controlArr.add(monotask);

        Assertions.assertEquals(controlArr, historyManager.getHistory());
    }

    @Test
    void remove() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        ArrayList<Task> controlArr = new ArrayList<>();

        historyManager.add(monotask);
        controlArr.add(monotask);

        historyManager.add(subtask);
        controlArr.add(subtask);

        historyManager.add(subtask1);
        controlArr.add(subtask1);

        historyManager.add(epic);
        controlArr.add(epic);

        historyManager.add(epic1);
        controlArr.add(epic1);

        historyManager.remove(subtask1.getId());
        controlArr.remove(subtask1);

        Assertions.assertEquals(controlArr, historyManager.getHistory());

        //Удаляем первый элемент
        historyManager.remove(monotask.getId());
        controlArr.remove(monotask);

        Assertions.assertEquals(controlArr, historyManager.getHistory());

        //Удаляем последний элемент
        historyManager.remove(epic1.getId());
        controlArr.remove(epic1);

        Assertions.assertEquals(controlArr, historyManager.getHistory());
    }

    @Test
    public void clearHistoryTest() {
        HistoryManager historyManager = new InMemoryHistoryManager();

        historyManager.add(monotask);
        historyManager.add(subtask);
        historyManager.add(subtask1);
        historyManager.add(epic);
        historyManager.add(epic1);

        historyManager.clearHistory();

        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void shouldBeEmptyListByEmptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }
}