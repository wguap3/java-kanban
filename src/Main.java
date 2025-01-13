public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", null, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", null, TaskStatus.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", null, null);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", null, null);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", null,epic1.getEpicId(), TaskStatus.DONE);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", null, epic1.getEpicId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", null,epic2.getEpicId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask3);

        taskManager.updateStatus(epic1.getEpicId());
        taskManager.updateStatus(epic2.getEpicId());

        System.out.println("Tasks:");
        System.out.println(taskManager.getAllTasks());

        System.out.println("Subtasks:");
        System.out.println(taskManager.getAllSubtask());

        System.out.println("Epics:");
        System.out.println(taskManager.getAllEpic());

        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        task2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task2);

        epic1.setStatus(null);
        epic2.setStatus(null);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        taskManager.updateStatus(epic1.getEpicId());
        taskManager.updateStatus(epic2.getEpicId());
        System.out.println("Tasks(update):");
        System.out.println(taskManager.getAllTasks());

        System.out.println("Subtasks(update):");
        System.out.println(taskManager.getAllSubtask());

        System.out.println("Epics(update):");
        System.out.println(taskManager.getAllEpic());

        taskManager.removeTask(task1.getId());
        System.out.println("Tasks(remove):");
        System.out.println(taskManager.getAllTasks());

        taskManager.removeEpic(epic1.getEpicId());
        System.out.println("Epics(remove):");
        System.out.println(taskManager.getAllEpic());

        System.out.println("Subtasks(remove):");
        System.out.println(taskManager.getAllSubtask());
    }
}



