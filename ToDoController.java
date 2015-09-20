import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.SubTask;
import model.Task;
import model.ToDoList;

/**
 * Controller for ToDoView.fxml
 *
 * @author Karan Patel
 */

public class ToDoController {

    //ObservableList to hold all lists in the program
    private ObservableList<ToDoList> toDoListModel = FXCollections.observableArrayList();

    public ObservableList<ToDoList> getData(){
        return toDoListModel;
    }

    public void initialize(){

        //disabling toDoListTasksInfoVBox and taskInfoVBox after the view loads up for the first time

        //user needs to have a ToDoList from todoListView selected for toDoListTasksInfoVBox to be enabled
        toDoListTasksInfoVBox.disableProperty().setValue(true);
        //user needs to have a task from the incompleteTaskListView selected for taskInfoVBox to be enabled
        taskInfoVBox.disableProperty().setValue(true);

        //setting editableProperty to false so that user cannot enter a date in custom format and needs to select date from datepicker
        taskDueDatePicker.editableProperty().setValue(false);

        todoListView.itemsProperty().set(toDoListModel);
    }

    @FXML
    private void showHideCompletedTask(){

    }

    @FXML
    private void deleteTask(ActionEvent e){

    }

    @FXML
    private Button clearDueDate;

    @FXML
    private Button saveNewTaskNameButton;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox toDoListTasksInfoVBox;

    @FXML
    private ListView<ToDoList> todoListView;

    @FXML
    private Button addTodoListButton;

    @FXML
    private TextField newTaskTextField;

    @FXML
    private ListView<Task> incompleteTaskListView;

    @FXML
    private Button showHideCompletedTaskButton;

    @FXML
    private ListView<Task> completeTaskListView;

    @FXML
    private VBox taskInfoVBox;

    @FXML
    private TextField taskNameTextField;

    @FXML
    private DatePicker taskDueDatePicker;

    @FXML
    private ToggleButton lowPriorityToggle;

    @FXML
    private ToggleGroup priority_toggle_group;

    @FXML
    private ToggleButton mediumPriorityToggle;

    @FXML
    private ToggleButton highPriorityToggle;

    @FXML
    private ListView<SubTask> subTaskListView;

    @FXML
    private TextField addSubTaskField;

    @FXML
    private TextArea taskNoteTextArea;

    @FXML
    private Button addSubTaskButton;

    @FXML
    private Button addTaskButton;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private TextField newToDoListTextField;

}

