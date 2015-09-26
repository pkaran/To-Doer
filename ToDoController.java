import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.SubTask;
import model.Task;
import model.ToDoList;

import java.time.LocalDate;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

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
        setAddToDoListButtonAndFieldHandler();

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

    //setting cell factory for todoListView, incompleteTaskListView and completeTaskListView
    private void setCellFactory(){

        todoListView.setCellFactory(e -> new CustomToDoListViewCell());
        incompleteTaskListView.setCellFactory(e -> new CustomTaskListViewCell());
        completeTaskListView.setCellFactory(e -> new CustomTaskListViewCell());
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

    //Cell factory for incompleteTaskListView and completeTaskListView
    //cell factory displays cell with following information about a task(if available):
    //Task name, task priority and task due date
    private class CustomTaskListViewCell extends  ListCell<Task>{

        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);
            updateViewModel();
        }

        private void updateViewModel(){

            setText(null);
            setGraphic(null);

            if(getItem() != null){

                BorderPane mainBox = new BorderPane();
                mainBox.setPadding(new Insets(3.0, 5.0, 3.0, 3.0));

                VBox taskNameDisplay = new VBox();
                taskNameDisplay.setAlignment(Pos.CENTER_LEFT);
                Text taskNameText = new Text();
                taskNameDisplay.getChildren().addAll(taskNameText);

                HBox priorityDueDateDisplay = new HBox();
                priorityDueDateDisplay.setAlignment(Pos.CENTER_RIGHT);
                Text taskPriorityText = new Text();
                Text taskDueDateText = new Text();
                taskPriorityText.setScaleX(1.25);
                taskPriorityText.setScaleY(1.25);
                priorityDueDateDisplay.setSpacing(40);
                priorityDueDateDisplay.getChildren().addAll(taskDueDateText, taskPriorityText);

                mainBox.leftProperty().set(taskNameDisplay);
                mainBox.rightProperty().set(priorityDueDateDisplay);

                mainBox.setMargin(mainBox.getLeft(), new Insets(0, 20, 0, 0));

                Task currentTask = getItem();
                int currentTaskPriority = currentTask.getPriority();

                //setting task name to be displayed by the cell
                taskNameText.setText(currentTask.getTaskTitle());

                //setting task due date to be displayed by the cell
                if(currentTask.getTaskDueDate() != null){

                    LocalDate currentDueDate = currentTask.getTaskDueDate();
                    taskDueDateText.setText(currentDueDate.getMonthValue() + "/" + currentDueDate.getDayOfMonth() + "/" + currentDueDate.getYear());
                }

                //setting task priority to be displayed by the cell
                switch (currentTaskPriority){
                    case 1:{
                        taskPriorityText.setText("!");
                        taskPriorityText.setFill(Color.web("#28ae33"));
                        break;

                    }
                    case 2:{
                        taskPriorityText.setText("!!");
                        taskPriorityText.setFill(Color.web("#babf23"));
                        break;
                    }
                    case 3:{
                        taskPriorityText.setText("!!!");
                        taskPriorityText.setFill(Color.web("#ee0606"));
                        break;
                    }
                    default:{
                    }

                }


                if(!currentTask.getComplete()){
                    taskNameText.setStrikethrough(false);
                }else{
                    //if task is completed, strike out the task name

                    mainBox.setOpacity(0.5);
                    taskNameText.setStrikethrough(true);
                }

                setText(null);
                setGraphic(mainBox);
            }

        }

    }

    //event handler for newToDoListTextField and addTodoListButton
    //event handlers will help the user to add new ToDo list to the program
    private void setAddToDoListButtonAndFieldHandler(){

        newToDoListTextField.addEventHandler(KEY_PRESSED, (event) -> {

            //if enter pressed, save To Do List
            if (event.getCode().equals(KeyCode.ENTER)) {

                if (!newToDoListTextField.getText().isEmpty()) {

                    ToDoList toAdd = new ToDoList(newToDoListTextField.getText());

                    //add new list to the top
                    toDoListModel.add(0, toAdd);
                    newToDoListTextField.clear();
                    //select the newly added list
                    todoListView.getSelectionModel().select(toAdd);
                    newTaskTextField.requestFocus();
                }
            }
        });

        //if addTodoListButton button pressed, add the new ToDo list in the newToDoListTextField to the program
        addTodoListButton.setOnAction(event -> {
            if (!newToDoListTextField.getText().isEmpty()) {

                ToDoList toAdd = new ToDoList(newToDoListTextField.getText());

                //add new list to the top
                toDoListModel.add(0, toAdd);
                newToDoListTextField.clear();
                //select the newly added list
                todoListView.getSelectionModel().select(toAdd);
                newTaskTextField.requestFocus();
            }
        });

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

