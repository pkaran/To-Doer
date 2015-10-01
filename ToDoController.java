import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
        setAddTaskButtonAndFieldHandler();
        setAddSubTaskButtonAndField();
        setTaskCompleteOrIncomplete();
        setSubTaskComplete();
        setSelectedToDoListListener();
        updateTaskView();

        //whenever date is changed for a task, the modified task is saved
        ChangeListener<? super LocalDate> dateChangeListener = ((observable1, oldValue1, newValue1) -> {
            saveTaskInfo(incompleteTaskListView.getSelectionModel().getSelectedItem());
        });



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

    //adding event handler for newToDoListTextField and addTodoListButton in the function
    //event handlers will help the user to add new Task to the program
    private void setAddTaskButtonAndFieldHandler(){

        //add new task to the program if newTaskTextField has focus and enter is pressed
        //new task added has the same name as the newTaskTextField's Text property
        newTaskTextField.addEventHandler(KEY_PRESSED, event -> {

            if(event.getCode().equals(KeyCode.ENTER)){

                if(!newTaskTextField.getText().isEmpty()){

                    incompleteTaskListView.getSelectionModel().clearSelection();
                    int selectedToDoListIndex = todoListView.getSelectionModel().getSelectedIndex();
                    toDoListModel.get(selectedToDoListIndex).getIncompleteTaskList().add(0, new Task(newTaskTextField.getText()));
                    incompleteTaskListView.getSelectionModel().select(0);
                    newTaskTextField.clear();
                }
            }

        });

        //if addTaskButton clicked, add the ew event to the program
        addTaskButton.setOnAction(event -> {
            if(!newTaskTextField.getText().isEmpty()){

                incompleteTaskListView.getSelectionModel().clearSelection();
                int selectedToDoListIndex = todoListView.getSelectionModel().getSelectedIndex();
                toDoListModel.get(selectedToDoListIndex).getIncompleteTaskList().add(0, new Task(newTaskTextField.getText()));
                incompleteTaskListView.getSelectionModel().select(0);
                newTaskTextField.clear();
            }
        });

    }

    //adding event handler for addSubTaskField and addSubTaskButton
    //event handlers will enable the user to add new sub tasks to a selected task
    private void setAddSubTaskButtonAndField(){

        //if addSubTaskField has focus and if enter pressed, add sub task to the program given that addSubTaskField is not empty
        addSubTaskField.addEventHandler(KEY_PRESSED, event -> {

            if (event.getCode().equals(KeyCode.ENTER)) {

                if (!addSubTaskField.getText().isEmpty()) {

                    incompleteTaskListView.getSelectionModel().getSelectedItem().getSubTasks().add(0, new SubTask(addSubTaskField.getText()));
                    subTaskListView.getSelectionModel().select(-1);
                    addSubTaskField.clear();
                }
            }

        });

        //if addSubTaskButton pressed, add sub task to the program
        addSubTaskButton.setOnAction(event -> {
            if (!addSubTaskField.getText().isEmpty()) {

                incompleteTaskListView.getSelectionModel().getSelectedItem().getSubTasks().add(0, new SubTask(addSubTaskField.getText()));
                subTaskListView.getSelectionModel().select(-1);
                addSubTaskField.clear();
            }
        });

    }

    //adding setOnMouseClicked event handler for incompleteTaskListView and completeTaskListView so that a task can be marked as
    //complete or incomplete
    //if a task in the incompleteTaskListView is double clicked, it will be marked as completed and moved to completeTaskListView and vice versa
    private void setTaskCompleteOrIncomplete(){

        incompleteTaskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {

                int selectedToDoList_Index = todoListView.getSelectionModel().getSelectedIndex();
                int selectedTaskEvent_Index = incompleteTaskListView.getSelectionModel().getSelectedIndex();

                if (selectedToDoList_Index >= 0 && selectedTaskEvent_Index >= 0) {

                    Task selectedTask = toDoListModel.get(selectedToDoList_Index).getIncompleteTaskList().get(selectedTaskEvent_Index);
                    toDoListModel.get(selectedToDoList_Index).getIncompleteTaskList().remove(selectedTaskEvent_Index);

                    selectedTask.setComplete(true);
                    toDoListModel.get(selectedToDoList_Index).getCompleteTaskList().add(0, selectedTask);

                }
            }
        });

        completeTaskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {

                int selectedToDoList_Index = todoListView.getSelectionModel().getSelectedIndex();
                int selectedTaskEvent_Index = completeTaskListView.getSelectionModel().getSelectedIndex();

                if (selectedToDoList_Index >= 0 && selectedTaskEvent_Index >= 0) {

                    Task selectedTask = toDoListModel.get(selectedToDoList_Index).getCompleteTaskList().get(selectedTaskEvent_Index);
                    toDoListModel.get(selectedToDoList_Index).getCompleteTaskList().remove(selectedTaskEvent_Index);

                    selectedTask.setComplete(false);
                    toDoListModel.get(selectedToDoList_Index).getIncompleteTaskList().add(0, selectedTask);

                }
            }
        });

    }

    //adding setOnMouseClicked event handler for subTaskListView so that a sub task can be marked complete
    //when a sub task in the subTaskListView is clicked more than twice, the sub task task will be marked complete and removed out of the subTaskListView
    private void setSubTaskComplete(){

        subTaskListView.setOnMouseClicked(event -> {

            if (event.getClickCount() >= 2) {
                incompleteTaskListView.getSelectionModel().getSelectedItem().getSubTasks().remove(subTaskListView.getSelectionModel().getSelectedItem());
                subTaskListView.getSelectionModel().select(-1);
            }
        });
    }

    //function adds a listner to todoListView's selected item so that the user selected todo list can be tracked and
    //completeTaskListView and incompleteTaskListView can be updated with selected todo list's corresponding tasks
    private void setSelectedToDoListListener(){

        ObservableList<ToDoList> selectedToDoListTask = todoListView.getSelectionModel().getSelectedItems();

        //listener to monitor change in selected To Do list
        selectedToDoListTask.addListener((Observable e) -> {

            ToDoList selectedToDo = todoListView.getSelectionModel().getSelectedItem();

            //if an item is selected
            if (selectedToDo != null) {

                if (toDoListTasksInfoVBox.disabledProperty().get()) {
                    toDoListTasksInfoVBox.disableProperty().set(false);
                }

                //set incompleteTaskListView to selected item's incomplete task list
                incompleteTaskListView.itemsProperty().setValue(selectedToDo.getIncompleteTaskList());

                //set completeTaskListView to selected item's complete task list
                completeTaskListView.itemsProperty().setValue(selectedToDo.getCompleteTaskList());

                incompleteTaskListView.requestFocus();

                incompleteTaskListView.getSelectionModel().clearSelection();

            } else {
                //if no item is selected
                incompleteTaskListView.setItems(null);
                completeTaskListView.setItems(null);
                toDoListTasksInfoVBox.disableProperty().set(true);
                taskInfoVBox.disableProperty().set(true);

            }
        });

    }

    //saves the task information from taskInfoVBox form to the task passed in as argument
    private void saveTaskInfo(Task task){

        //to track if the task's attributes have been changed
        boolean differentTaskName = false, differentDueDate = false, differentPriority = false;

        if(task == null){
            return;
        }

        //if different title
        if(!task.getTaskTitle().equals(taskNameTextField.getText()))
            differentTaskName = true;

        //if different due date
        if(task.getTaskDueDate() == null){
            if(taskDueDatePicker.getValue() != null){
                differentDueDate = true;
            }
        }else{

            if(taskDueDatePicker.getValue() == null){
                differentDueDate = true;
            }

            if(taskDueDatePicker.getValue() != null){
                if(!(task.getTaskDueDate().isEqual(taskDueDatePicker.getValue())))
                    differentDueDate = true;
            }
        }


        int currentTaskPriority = task.getPriority();

        //if no priority selected for the task
        if(priority_toggle_group.getSelectedToggle() == null){
            if(currentTaskPriority != 0){
                differentPriority = true;
            }
        }else{
            //if different priority
            if(currentTaskPriority != (int) priority_toggle_group.getSelectedToggle().getUserData())
                differentPriority = true;
        }

        //if task attributes have been changed, replace current task with new task with updated task attributes
        if(differentTaskName || differentDueDate || differentPriority){

            Task replacementTask = new Task();

            replacementTask.setTaskTitle(taskNameTextField.getText());
            replacementTask.setTaskDueDate(taskDueDatePicker.getValue());
            if(priority_toggle_group.getSelectedToggle() == null){
                replacementTask.setPriority(0);
            }else{
                replacementTask.setPriority((int) priority_toggle_group.getSelectedToggle().getUserData());
            }
            replacementTask.setSubTasks(task.getSubTasks());
            replacementTask.setNote(taskNoteTextArea.getText());

            int currentListIndex = -1, currentTaskIndex = -1;            //to track current index
            int replacementListIndex = -1, replacementTaskIndex = -1;   //to track index where replacement task will placed at

            found:{
                for(ToDoList currentList : toDoListModel){

                    currentListIndex++;
                    currentTaskIndex = -1;

                    for(Task currentTask : currentList.getIncompleteTaskList()){
                        currentTaskIndex++;

                        //if task's unique ID matches current task, assign the current task's list index and task index to the replacement index variables
                        //and break out of the loop
                        if(currentTask.getUniqueID() == task.getUniqueID()){
                            replacementListIndex = currentListIndex;
                            replacementTaskIndex = currentTaskIndex;

                            break found;
                        }

                    }
                }
            }

            //if found the task to replace with
            if(replacementListIndex != -1 && replacementTaskIndex != -1){
                toDoListModel.get(replacementListIndex).getIncompleteTaskList().set(replacementTaskIndex, replacementTask);

                //if task to be replaced is the first task in the first to do list, select the replaced task and update
                //incompleteTaskListView
                //code below help to get around a bug in selection model for list view
                if(replacementListIndex == 0 && replacementTaskIndex == 0){
                    todoListView.getSelectionModel().select(replacementListIndex);
                    incompleteTaskListView.getSelectionModel().select(replacementListIndex);
                }

            }

        }
        //if task's priority, name and due date have not been changed, then only update the task's (one that is passed in as argument) note
        else{

            task.setNote(taskNoteTextArea.getText());
        }

    }

    //updates view based on following actions :
    //when a user changes a task's priority, modified task is saved
    //when a user types in the taskNameTextField, enable the saveNewTaskNameButton
    //added listener for saveNewTaskNameButton (when a user clicks it, the task is updated) and clearDueDate
    private void updateTaskView(){

        int selectedToDoListIndex = todoListView.getSelectionModel().getSelectedIndex();
        int selectedTaskIndex = incompleteTaskListView.getSelectionModel().getSelectedIndex();

        EventHandler<ActionEvent> toggleAction = observable -> {

            saveTaskInfo(incompleteTaskListView.getSelectionModel().getSelectedItem());
        };

        lowPriorityToggle.setOnAction(toggleAction);
        mediumPriorityToggle.setOnAction(toggleAction);
        highPriorityToggle.setOnAction(toggleAction);

        taskNameTextField.setOnKeyPressed(event -> {

            saveNewTaskNameButton.disableProperty().setValue(false);
        });

        saveNewTaskNameButton.setOnAction(event -> {

            saveTaskInfo(incompleteTaskListView.getSelectionModel().getSelectedItem());
        });

        clearDueDate.setOnAction(event -> {

            taskDueDatePicker.setValue(null);
            saveTaskInfo(incompleteTaskListView.getSelectionModel().getSelectedItem());
        });
    }

    //sets data for taskInfoVBox's nodes/components using task's data (passed as argument)
    private void setTaskInfoVBoxData(Task task, ChangeListener<? super LocalDate> dateChangeListener) {

        saveNewTaskNameButton.disableProperty().setValue(true);

        taskNameTextField.setText(task.getTaskTitle());

        if(task.getTaskDueDate() != null){
            taskDueDatePicker.setValue(task.getTaskDueDate());
        }

        taskDueDatePicker.valueProperty().addListener(dateChangeListener);

        int taskPriority = task.getPriority();

        switch(taskPriority){
            case 1:{
                lowPriorityToggle.setSelected(true);
                break;
            }
            case 2: {
                mediumPriorityToggle.setSelected(true);
                break;
            }
            case 3:{
                highPriorityToggle.setSelected(true);
                break;
            }
        }

        subTaskListView.setItems(task.getSubTasks());

        taskNoteTextArea.setText(task.getNote());

    }

    //if showHideCompletedTaskButton clicked, show the completeTaskListView if it is hidden and vice versa
    @FXML
    private void showHideCompletedTask(){

        if(completeTaskListView.disabledProperty().get()){
            completeTaskListView.disableProperty().setValue(false);
            completeTaskListView.setVisible(true);

            showHideCompletedTaskButton.setText("Hide Completed Tasks");
        }else{

            completeTaskListView.disableProperty().setValue(true);
            completeTaskListView.setVisible(false);

            showHideCompletedTaskButton.setText("Show Completed Tasks");

        }

    }

    //deletes the selected task when the button is pressed
    @FXML
    private void deleteTask(ActionEvent e){

        ToDoList selectedList = todoListView.getSelectionModel().getSelectedItem();
        Task toDelete = incompleteTaskListView.getSelectionModel().getSelectedItem();

        if(!selectedList.getIncompleteTaskList().remove(toDelete)){
            System.err.println("Task not deleted");
        }
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

