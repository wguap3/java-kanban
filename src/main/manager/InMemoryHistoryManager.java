package main.manager;

import main.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyStorage = new ArrayList<>();
    private final Map<Integer, Node<Task>> tasks = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            removeNode(tasks.get(task.getId()));
        }
        linkLast(task);
        tasks.put(task.getId(), tail);

        historyStorage.clear();
        historyStorage.addAll(getTasks());

    }

    @Override
    public List<Task> getHistory() {
        return historyStorage;
    }

    @Override
    public void remove(int id) {
        if (tasks.containsKey(id)) {
            removeNode(tasks.get(id));
            tasks.remove(id);
            historyStorage.clear();
            historyStorage.addAll(getTasks());


        }

    }

    public void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        tasks.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> tasksL = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasksL.add(current.data);
            current = current.next;
        }
        return tasksL;
    }

    public void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

}