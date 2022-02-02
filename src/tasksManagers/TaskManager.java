package tasksManagers;

import tasks.EpicTask;
import tasks.MonoTask;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    /**
     * Добавить новую задачу
     *
     * @param task
     * @return boolean
     * Метод вернет true, если задача была успешно добавлена.
     * Если в хранилище найдется задача с таким же индексом как у новой задачи,
     * метод вернет false и добавления не будет.
     */
    boolean addNewTask(Task task);

    /**
     * Обновить задачу
     *
     * @param task
     * @return boolean
     * Метод вернет false: если искомый id не был найден и
     * если тип переданной задачи не совпадает с найденной по id задачей
     */
    boolean updateTask(Task task, int id);

    List<MonoTask> getAllMonotask();


    List<EpicTask> getAllEpics();

    /**
     * Вернуть сабтаски определенной задачи
     *
     * @param epicId
     * @return Вернет null, если задачи с переданным id нет в хранилище
     */
    List<Subtask> getSubtasksDefinedEpic(int epicId);

    /**
     * Получить задачу любого типа (Subtask, EpicTask, Monotask) по id
     *
     * @param id
     * @return Метод вернет null, если задача не найдена
     */
    Task getTaskById(int id);

    /**
     * Удалить задачу по id
     *
     * @param id
     * @return Метод вернет false если задача с переданным id не была найдена
     */
    boolean removeTaskById(int id);

    void removeAllTasks();

    /**
     * Получить Subtask по id
     *
     * @param id
     * @return Вернет null если задача с переданным id не найден
     */
    Subtask getSubtask(int id);

    /**
     * Получить EpicTask по id
     *
     * @param id
     * @return Вернет null если задача с переданным id не найден
     */
    EpicTask getEpic(int id);

    List<Task> history();
}
