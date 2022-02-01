package tests.history;

import generators.TaskGenerator;
import history.History;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;

class historyTest {

    @Test
    void linkLastTest() {
        History history = new History();
        TaskGenerator taskGenerator = new TaskGenerator();
        TaskManager taskManager = Managers.getDefault();

        Task epic1 = taskGenerator.generateEpicTask();
        taskManager.addNewTask(epic1);

        Task subtask11 = taskGenerator.generateSubtask(epic1.getId());
        taskManager.addNewTask(subtask11);

        Task subtask12 = taskGenerator.generateSubtask(epic1.getId());
        taskManager.addNewTask(subtask12);

        Task subtask13 = taskGenerator.generateSubtask(epic1.getId());
        taskManager.addNewTask(subtask13);

        Task epic2 = taskGenerator.generateEpicTask();
        taskManager.addNewTask(epic2);

        /*
        Проверяем добавление элементов
         */
        history.linkLast(epic1);
        history.linkLast(subtask11);
        history.linkLast(epic2);
        history.linkLast(subtask12);
        history.linkLast(subtask13);

        ArrayList<Task> controlArr = new ArrayList<>();
        controlArr.add(epic1);
        controlArr.add(subtask11);
        controlArr.add(epic2);
        controlArr.add(subtask12);
        controlArr.add(subtask13);

        Assertions.assertEquals(controlArr, history.getTasks());

        /*
        Добавляем epic2 еще раз в историю, и перемещаем в контрольном списке epic2 в конец
         */
        history.linkLast(epic2);
        controlArr.remove(epic2);
        controlArr.add(epic2);

        Assertions.assertEquals(controlArr, history.getTasks());

        /*
        Проверяем удаление
         */
        controlArr.remove(subtask13);
        history.removeNodeById(subtask13.getId());

        Assertions.assertEquals(controlArr, history.getTasks());

        /*
        Проверяем удаление Subtask-ов вместе с Epic-ми
         */
        controlArr.remove(epic1);
        controlArr.remove(subtask11);
        controlArr.remove(subtask12);
        controlArr.remove(subtask13);
        history.removeNodeById(epic1.getId());

        Assertions.assertEquals(controlArr, history.getTasks());
    }
}