package tests.historyManagers;

import generators.TaskGenerator;
import history.managers.HistoryManager;
import history.managers.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    TaskGenerator taskGenerator = new TaskGenerator();
    Task monotask = taskGenerator.generateMonotask();
    Task epic = taskGenerator.generateEpicTask();
    Task epic1 = taskGenerator.generateEpicTask();
    Task subtask = taskGenerator.generateSubtask(epic.getId());
    Task subtask1 = taskGenerator.generateSubtask(epic.getId());

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
}