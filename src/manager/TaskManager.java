package manager;

import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    /**
     * Метод вернет true, если задача была успешно добавлена.
     * Если в хранилище найдется задача с таким же индексом как у новой задачи,
     * метод вернет false и добавления не будет.
     *
     * @param task
     * @return boolean
     */
    public boolean addNewTask(Task task);

    /**
     * Метод вернет false: если искомый id не был найден,
     * если тип переданной задачи не совпадает с найденной по id задачей
     *
     * @param task
     * @return boolean
     */
    public boolean updateTask(Task task, int id);

    public ArrayList<MonoTask> getAllMonotask();


    public ArrayList<EpicTask> getAllEpics();

    /**
     * Вернет null, если задачи с переданным id нет в хранилище
     *
     * @param epicId
     * @return
     */
    public ArrayList<Subtask> getSubtasksDefinedEpic(int epicId);

    /**
     * Метод вернет null, если задачи с переданным id нет
     *
     * @param id
     * @return Task
     */
    public Task getTaskById(int id);

    /**
     * Метод вернет true, если задача была ранее добавлена
     *
     * @param id
     * @return
     */
    public boolean removeTaskById(int id);

    public void removeAllTasks();

    /**
     * Вернет null если объект типа Subtask не был найден
     *
     * @param id
     * @return
     */
    public Subtask getSubtask(int id);

    /**
     * Вернет null если объект типа EpicTask не был найден
     *
     * @param id
     * @return
     */
    public EpicTask getEpic(int id);

    public ArrayList<Task> history();

}
