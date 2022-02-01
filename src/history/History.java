package history;

import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class History<T extends Task> {
    final static private int HISTORY_CAPACITY = 10;
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private HashMap<Integer, Node> historyMap;

    public History() {
        head = null;
        tail = null;
        size = 0;
        historyMap = new HashMap<>();
    }

    public void linkLast(T newTask) {
        Node<T> newNode = new Node<>(newTask);

        if (head == null) {
            head = newNode;
        }
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        writeToHistoryMap(newNode);
        size++;

        if (size > HISTORY_CAPACITY) {
            removeFirstNode();
        }
    }

    private void writeToHistoryMap(Node<T> node) {
        if (historyMap.containsKey(node.data.getId())) {
            removeNodeById(node.data.getId());
        }
        historyMap.put(node.data.getId(), node);
    }

    /**
     * Удалить первый элемент
     */
    private void removeFirstNode() {
        Node<T> secondNode = head.next;

        historyMap.remove(head.data.getId());
        head.data = null;
        head.next = null;
        head = secondNode;
        size--;
    }

    /**
     * Удалить звено по id
     * Метод удаляет звено, которое содержит задачу с переданным id
     *
     * @param id
     */
    public void removeNodeById(int id) {
        Node<T> node = historyMap.get(id);

        if (node == null) {
            return;
        }

        if (node.data instanceof EpicTask) {
            HashMap<Integer, Subtask> subtasks = ((EpicTask) node.data).getSubtasks();
            for (int subtaskId : subtasks.keySet()) {
                removeNodeById(subtaskId);
            }
        }

        Node<T> prevNode = node.prev;
        Node<T> nextNode = node.next;

        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        } else if (prevNode == null) {
            head = nextNode;
        } else if (nextNode == null) {
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;
        node.data = null;

        size--;
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> result = new ArrayList<>();
        Node<T> node = head;

        while (node != null) {
            result.add(node.data);
            node = node.next;
        }

        return result;

    }
}

