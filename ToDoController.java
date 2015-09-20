import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

        setPriorityTogglesUserData();
        setPlaceHolder();
        setSelectionModel();
        setCellFactory();
    }

    //setting user data for priority toggles
    private void setPriorityTogglesUserData(){

        lowPriorityToggle.setUserData(1);
        mediumPriorityToggle.setUserData(2);
        highPriorityToggle.setUserData(3);
    }

    //setting place holder for todoListView, subTaskListView, completeTaskListView and incompleteTaskListView
    private void setPlaceHolder() {

        todoListView.setPlaceholder(new Label("Add a new To Do list from below ..."));
        subTaskListView.setPlaceholder(new Label("No subtask to display"));
        completeTaskListView.setPlaceholder(new Label("No complete task to display"));
        incompleteTaskListView.setPlaceholder(new Label("No incomplete task. Enjoy your day !"));
    }

    //setting selection model for todoListView, completeTaskListView, incompleteTaskListView and subTaskListView
    private void setSelectionModel(){

        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        completeTaskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        incompleteTaskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        subTaskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    //setting cell factory for todoListView
    private void setCellFactory(){

        todoListView.setCellFactory(e -> new CustomToDoListViewCell());
    }

    //Cell factory for todoListView
    //When in edit mode, an option for editing represented ToDO List's name and deleting it is displayed by the cell
    private class CustomToDoListViewCell extends ListCell<ToDoList>{

        @Override
        protected void updateItem(ToDoList item, boolean empty) {
            super.updateItem(item, empty);
            updateViewModel();
        }

        private void updateViewModel(){

            setText(null);
            setGraphic(null);

            if(isEditing()){
                VBox mainBox = new VBox();

                HBox changeToDoListNamebox = new HBox();
                TextField listNameField = new TextField();
                listNameField.setPromptText("Enter new list name ...");
                Button setNameChange = new Button("Set");
                changeToDoListNamebox.getChildren().addAll(listNameField, setNameChange);

                Button deleteList = new Button("Delete List");
                Button cancelEdit = new Button("Cancel");

                if(getItem() != null){
                    listNameField.setText(getItem().getListName());
                }

                setNameChange.setOnAction(e -> {

                    if(!getItem().getListName().equals(setNameChange.getText())){
                        commitEdit(new ToDoList(listNameField.getText(), getItem().getCompleteTaskList(), getItem().getIncompleteTaskList()));
                        todoListView.getSelectionModel().select(getIndex());
                    }
                });

                deleteList.setOnAction(e -> {

                    int item_to_delete_index = getIndex();
                    int size_of_data_model = toDoListModel.size();

                    if(size_of_data_model > 1){

                        if(item_to_delete_index == 0){
                            todoListView.getSelectionModel().select(1);
                        }else{
                            todoListView.getSelectionModel().select(--item_to_delete_index);
                        }

                    }

                    //remove deleted list from toDoListModel
                    toDoListModel.remove(getIndex());
                });

                cancelEdit.setOnAction(e -> {
                    cancelEdit();
                });

                mainBox.getChildren().addAll(changeToDoListNamebox, deleteList, cancelEdit);

                setGraphic(mainBox);
            }else{

                if(getItem() != null){
                    setText(getItem().getListName());
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            updateViewModel();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            updateViewModel();
        }
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

