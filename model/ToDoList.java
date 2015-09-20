package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Definition of ToDOList class
 * ToDoList 'has a' list name and a list of complete and incomplete tasks
 *
 * @author Karan Patel
 */

public class ToDoList {

    private String listName;
    private ObservableList<Task> completeTaskList;
    private ObservableList<Task> incompleteTaskList;

    public ToDoList(){
        this("");
    }

    public ToDoList(String listName){
        this(listName, FXCollections.observableArrayList(), FXCollections.observableArrayList());
    }

    public ToDoList(String listName, ObservableList<Task> completeTaskList, ObservableList<Task> incompleteTaskList){
        this.listName = listName;
        this.completeTaskList = completeTaskList;
        this.incompleteTaskList = incompleteTaskList;
    }

    //overridden toString() returns listName
    @Override
    public String toString() {
        return listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ObservableList<Task> getCompleteTaskList() {
        return completeTaskList;
    }

    public void setCompleteTaskList(ObservableList<Task> completeTaskList) {
        this.completeTaskList = completeTaskList;
    }

    public ObservableList<Task> getIncompleteTaskList() {
        return incompleteTaskList;
    }

    public void setIncompleteTaskList(ObservableList<Task> incompleteTaskList) {
        this.incompleteTaskList = incompleteTaskList;
    }
}
