package main.manager;

import main.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
        history.put(task.getId(), tail);

    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasksL = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasksL.add(current.data);
            current = current.next;
        }
        return tasksL;
    }

    private void removeNode(Node<Task> node) {
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