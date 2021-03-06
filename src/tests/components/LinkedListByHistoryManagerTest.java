package tests.components;

import components.LinkedListByHistoryManager;
import components.Node;
import generators.TaskGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.ArrayList;

class LinkedListByHistoryManagerTest {
    private static TaskGenerator taskGenerator ;
    private static Task monotask1;
    private static Task monotask2;
    private static Task monotask3;
    private static Task monotask4;

    @BeforeAll
    public static void beforeAll() {
        taskGenerator = new TaskGenerator();
        monotask1 = taskGenerator.generateMonotask();
        monotask2 = taskGenerator.generateMonotask();
        monotask3 = taskGenerator.generateMonotask();
        monotask4 = taskGenerator.generateMonotask();
    }

    @Test
    void linkLast() {
        LinkedListByHistoryManager<Task> linkedList = new LinkedListByHistoryManager<>();
        ArrayList<Task> controlArr = new ArrayList<>();

        linkedList.linkLast(monotask1);
        controlArr.add(monotask1);

        linkedList.linkLast(monotask2);
        controlArr.add(monotask2);

        linkedList.linkLast(monotask3);
        controlArr.add(monotask3);

        Assertions.assertEquals(controlArr, linkedList.getTasks());
    }

    @Test
    void removeNode() {
        LinkedListByHistoryManager<Task> linkedList = new LinkedListByHistoryManager<>();
        ArrayList<Task> controlArr = new ArrayList<>();

        Node<Task> node1 = linkedList.linkLast(monotask1);
        Node<Task> node2 = linkedList.linkLast(monotask2);
        Node<Task> node3 = linkedList.linkLast(monotask3);
        Node<Task> node4 = linkedList.linkLast(monotask4);

        controlArr.add(monotask1);
        controlArr.add(monotask2);
        controlArr.add(monotask3);
        controlArr.add(monotask4);

        //Удаляем первый эелемент списка
        linkedList.removeNode(node1);
        controlArr.remove(monotask1);

        Assertions.assertEquals(controlArr, linkedList.getTasks());

        //Удаляем последний элемент списка
        linkedList.removeNode(node4);
        controlArr.remove(monotask4);

        Assertions.assertEquals(controlArr, linkedList.getTasks());

    }
}