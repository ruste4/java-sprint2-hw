package components;

import tasks.Task;

import java.util.ArrayList;

public class LinkedListByHistoryManager<T extends Task> {
    private Node<T> head;
    private Node<T> tail;
    private int size;


    public LinkedListByHistoryManager() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Вставить задачу в конец списка задач
     *
     * @param task
     * @return Возвращает ссылку на созданный и добавленный в конец элемент списка
     */
    public Node<T> linkLast(T task) {
        Node<T> newNode = new Node<>(task);

        if (head == null) {
            head = newNode;
        }
        if (tail != null) {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        size++;

        return newNode;
    }

    /**
     * Удалить элемент списка
     *
     * @param node
     * @return Возвращает содержимое удаленного элемента
     */
    public T removeNode(Node<T> node) {
        if (node == null) {
            return null;
        }

        Node<T> prevNode = node.prev;
        Node<T> nextNode = node.next;

        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        } else if (prevNode == null && nextNode != null) {
            nextNode.prev = null;
            head = nextNode;
        } else if (nextNode == null && prevNode != null) {
            prevNode.next = null;
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;
        size--;

        return node.data;
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

    public T removeFirstNode() {
        return removeNode(head);
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
