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
            tail.setNext(newNode);
            newNode.setPrev(tail);
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

        Node<T> prevNode = node.getPrev();
        Node<T> nextNode = node.getNext();

        if (prevNode != null && nextNode != null) {
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        } else if (prevNode == null && nextNode != null) {
            nextNode.setPrev(null);
            head = nextNode;
        } else if (nextNode == null && prevNode != null) {
            prevNode.setNext(null);
            tail = prevNode;
        }

        node.setPrev(null);
        node.setNext(null);
        size--;

        return node.getData();
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> result = new ArrayList<>();
        Node<T> node = head;

        while (node != null) {
            result.add(node.getData());
            node = node.getNext();
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
