package history.managers;

import components.LinkedListByHistoryManager;
import components.Node;
import tasks.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_CAPACITY = 10;
    private HashMap<Integer, Node<Task>> historyMap;
    private LinkedListByHistoryManager<Task> linkedListByHistoryManager;

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
        this.linkedListByHistoryManager = new LinkedListByHistoryManager<>();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node<Task> addedNode = linkedListByHistoryManager.linkLast(task);
        historyMap.put(task.getId(), addedNode);

        if (HISTORY_CAPACITY < linkedListByHistoryManager.getSize()) {
            Task firstElement = linkedListByHistoryManager.removeFirstNode();
            historyMap.remove(firstElement.getId());
        }
    }

    @Override
    public void remove(int id) {
        if (!historyMap.containsKey(id)) {
            return;
        }
        Node<Task> node = historyMap.get(id);
        linkedListByHistoryManager.removeNode(node);
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return linkedListByHistoryManager.getTasks();
    }

    @Override
    public void clearHistory() {
        linkedListByHistoryManager.clear();
        historyMap.clear();
    }
}

