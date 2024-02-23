package alastor.command;

import alastor.AlastorException;
import alastor.Storage;
import alastor.TaskList;
import alastor.Ui;
import alastor.task.Task;

/**
 * Represents a command to mark or unmark a task.
 */
public class MarkCommand extends Command {

    private final int index;
    private final boolean isMark;

    /**
     * Constructs a MarkCommand.
     *
     * @param index Index of task to be marked or unmarked.
     * @param isMark Whether to mark or unmark the task.
     */
    public MarkCommand(int index, boolean isMark) {
        this.index = index;
        this.isMark = isMark;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws AlastorException {
        if (index < 1 || index > tasks.size()) {
            throw new AlastorException("I'm afraid the task number you've entered is invalid.");
        }
        Task task = tasks.get(index - 1);
        if (isMark) {
            task.mark();
            assert (task.getStatusIcon().equals("[X]")) : "Task should be marked";
        } else {
            task.unmark();
            assert (task.getStatusIcon().equals("[ ]")) : "Task should be unmarked";
        }
        storage.saveRewrite(tasks);
        return ui.showMark(task, this.isMark);
    }
}
