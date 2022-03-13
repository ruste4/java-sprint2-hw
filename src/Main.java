import generators.TaskGenerator;
import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;

public class Main {
    public static void main(String[] args) {
        TaskGenerator tg = new TaskGenerator();
        MonoTask monotask = tg.generateMonotask();
        EpicTask epicTask = tg.generateEpicTask();
        Subtask subtask = tg.generateSubtask(epicTask.getId());

        System.out.println(monotask);
        System.out.println(epicTask);
        System.out.println(subtask);
    }
}
