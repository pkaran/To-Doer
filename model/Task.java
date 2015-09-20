package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

/**
 * Definition of Task class
 * Task 'has a' task name, priority, due date, list of sub-tasks, task note, completion marker and a unique task ID
 *
 * @author Karan Patel
 */

public class Task {

    private static long taskCounter = 0;  //used to generate unique ID for every task
    final private long uniqueID;          //each task is given a unique ID, ID # starts from 0 and is incremented by 1 for next initialized task

    private StringProperty taskTitle;           //title/name of the task
    private IntegerProperty priority;
    private ObservableList<SubTask> subTasks;   //list of sub tasks for the task
    private String note;
    private BooleanProperty complete;           //to indicate if a task has been accomplished or not
    private LocalDate taskDueDate;

    public Task() {
        this("");
    }

    public Task(String taskTitle) {
        this(taskTitle, 0, FXCollections.observableArrayList(), "", false, null);
    }

    public Task(String taskTitle, int priority, ObservableList<SubTask> subTasks, String note, boolean complete, LocalDate taskDueDate) {
        this.taskTitle = new SimpleStringProperty(taskTitle);
        this.priority = new SimpleIntegerProperty(priority);
        this.subTasks = subTasks;
        this.note = note;
        this.complete = new SimpleBooleanProperty(complete);
        this.taskDueDate = taskDueDate;

        uniqueID =  taskCounter++;
    }

    //overridden toString() returns taskTitle
    @Override
    public String toString() {
        return taskTitle.get();
    }

    public final String getTaskTitle() {
        return taskTitle.get();
    }

    public StringProperty taskTitleProperty() {
        return taskTitle;
    }

    public final void setTaskTitle(String taskTitle) {
        this.taskTitle.set(taskTitle);
    }

    public final int getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public final void setPriority(int priority) {
        this.priority.set(priority);
    }

    public ObservableList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ObservableList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final boolean getComplete() {
        return complete.get();
    }

    public BooleanProperty completeProperty() {
        return complete;
    }

    public final void setComplete(boolean complete) {
        this.complete.set(complete);
    }

    public LocalDate getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(LocalDate taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public long getUniqueID() {
        return uniqueID;
    }
}
