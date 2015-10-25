import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import model.SubTask;
import model.Task;
import model.ToDoList;

import java.io.*;
import java.time.LocalDate;

/**
 * Starting point for application defining stage and setting properties for the stage
 *
 * @author Karan Patel
 */

public class ToDoer extends Application{

    final String PROGRAM_NAME = "To Doer";
    final String ICON_PATH = "media/icon.jpg";
    final String FXML_SCENE_HIERARCHY_PATH = "TodoView.fxml";
    final String DATA_FILE_PATH = "App-Data.txt";

    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SCENE_HIERARCHY_PATH));
        ScrollPane scene_root = loader.load();
        ToDoController controller = loader.getController();
        ObservableList<ToDoList> data = controller.getData();

		//file pointer to file (called App-Data.txt) used for loading and saving data
        File file = new File(DATA_FILE_PATH);
        //loads previously saved data from file into data variable passed in as argument
        loadData(data, file);
		
        Image primaryStage_icon = new Image(ICON_PATH);
        primaryStage.getIcons().add(primaryStage_icon);
        primaryStage.setScene(new Scene(scene_root));
        primaryStage.setTitle(PROGRAM_NAME);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();

		//triggered when application is closed
        primaryStage.setOnCloseRequest(event -> {

            //saves note for task selected by the user in the memory
            controller.saveCurrentSelectedTaskNote();
            //saves application data (data about all task lists and their tasks) from memory to file
            saveData(data, file);
        });

    }

    public static void main(String [] args){
        launch(args);
    }

    //saves information stored in data variable passed as argument (which contains data for all task lists and their tasks) into file
    //if file doesn't exist, a new file is created by the function (this is true for Windows OS, not tested for any other OS)
    private void saveData(ObservableList<ToDoList> data, File file){

        if(data == null || file == null){
            return;
        }

        //writer to be used for writing first line to the file
        BufferedWriter writer = null;
        //except first line, all lines shall be written to the file using appendWriter
        BufferedWriter appendWriter = null;
        //if data is written to file using writer, set writtenWithWriter to true, else set it to false
        boolean writtenWithWriter = false;
        //line to write to the file
        String line;
        //taskInfo is used to hold information about a task in the form of strings
        String [] taskInfo;

        try {

            writer = new BufferedWriter(new FileWriter(file));
            appendWriter = new BufferedWriter(new FileWriter(file, true));

            //for each list
            for(ToDoList list : data){

                //if list exists
                if(list != null){

                    //add list title
                    line = "!$$;;;" + list.getListName();

                    //if no data is written to the file using writer , then write data to file using writer
                    //else, use appendWriter
                    if(!writtenWithWriter){

                        writer.write(line);     //if file doesn't exist, a new one is created (true for windows OS, not tested for any other OS)
                        writer.newLine();

                        writtenWithWriter = true;
                    }else{

                        appendWriter.write(line);
                        appendWriter.newLine();
                    }

                    if(list.getIncompleteTaskList() != null){

                        //for each task in Incomplete Task List
                        for(Task task : list.getIncompleteTaskList()){

                            //retrieve task information in the form of strings (which are stored in taskInfo) using taskInfoToString
                            //if taskInfo is not null, write it to the file
                            if((taskInfo = taskInfoToString(task, false)) != null){

                                appendWriter.write(taskInfo[0]);
                                appendWriter.newLine();
                                appendWriter.write(taskInfo[1]);
                                appendWriter.newLine();
                            }
                        }
                    }

                    if(list.getCompleteTaskList() != null){

                        //for each task in Complete Task List
                        for(Task task : list.getCompleteTaskList()){

                            //retrieve task information in the form of strings (which are stored in taskInfo) using taskInfoToString
                            //if taskInfo is not null, write it to the file
                            if((taskInfo = taskInfoToString(task, true)) != null){

                                appendWriter.write(taskInfo[0]);
                                appendWriter.newLine();
                                appendWriter.write(taskInfo[1]);
                                appendWriter.newLine();
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {

            e.printStackTrace();        //for testing purpose
        }
        finally {

            //close writer and appendWriter if they are not already closed
            try {

                if (writer != null)
                    writer.close();

                if (appendWriter != null)
                    appendWriter.close();
            }
            catch (IOException e){

                e.printStackTrace();        //for testing purpose
            }
        }
    }

    //store task's information in the form of 2 strings and return an array of string generated
    //first string (at index 0 of returned array) will have information representing task's completion status, title, due date, priority and note
    //example strings :
    //!--;;;Incomplete Task Title;;;10,24,2015;;;2;;;Task Note
    //!++;;;Complete Task Title;;;10,24,2015;;;2;;;Task Note
    //second string (at index 1 of returned array) will have information representing task's sub tasks
    //example string :
    //![];;;Subtask 2;;;Subtask 1
    private String [] taskInfoToString(Task task, boolean taskComplete){

        if(task == null){

            return null;
        }

        //array of string to store task information
        String [] info = new String[2];
        //to store task's due date
        LocalDate taskDate;

        //add task's completion status and title to info[0]
        if(taskComplete){

            info[0] = "!++;;;" + task.getTaskTitle();       //if task is complete
        }else{

            info[0] = "!--;;;" + task.getTaskTitle();       //if task in incomplete
        }

        //add task's due date
        if(task.getTaskDueDate() != null){

            taskDate = task.getTaskDueDate();
            info[0] += ";;;" + taskDate.getMonthValue() + "," + taskDate.getDayOfMonth() + "," + taskDate.getYear();
        }else{

            info[0] += ";;;!*---*";                         //if task does not have a due date
        }

        //add task priority
        info[0] += ";;;" + task.getPriority();

        //add task note
        if(task.getNote() == null || task.getNote().equals("")){

            info[0] += ";;;!*---*";
        }else{

            info[0] += ";;;" + task.getNote();
        }

        //add task's sub task(s)
        if(task.getSubTasks() == null || task.getSubTasks().size() == 0){

            info[1] = "![];;;!*---*";           //if task does not have any sub task
        }else{

            info[1] = "![]";

            for(SubTask subTask : task.getSubTasks()){

                if(subTask != null){

                    //append task's all sub task to the string
                    info[1] += ";;;" + subTask.getTitle();
                }
            }
        }

        //return array of string containing task's information
        return info;
    }

    //load data from file into data variable (memory)
    //if file pointed out by file variable doesn't exist, function loads sample data into the data variable (happens when app is used for the first time)
    private void loadData(ObservableList<ToDoList> data, File file){

        if(data == null || file == null){

            return;
        }
		
		//if file pointed by file pointer does not exist, load sample data into data variable
            if(file != null && !file.exists()){

                ToDoList defaultList = new ToDoList("Default List");
                Task sampleTask = new Task("Sample Task (Double click me to mark complete)");

                sampleTask.setPriority(2);
                sampleTask.getSubTasks().add(new SubTask("Double click to delete"));
                sampleTask.setTaskDueDate(LocalDate.of(2015, 12, 15));
				sampleTask.setNote("Double click on a To-Do List's \nname to delete the list or to \nchange list's name");

                defaultList.getIncompleteTaskList().add(sampleTask);
                data.add(defaultList);
            }

        //used to read data from file
        BufferedReader reader = null;

        try{

            reader = new BufferedReader(new FileReader(file));
            //used to extract data from file line by line
            String line;
            //used to track index of ToDo List
            int currentToDoListIndex = -1;
            //used to store and recognize kind of information represented by a line read from file
            String identifier;

            while ((line = reader.readLine()) != null) {

                String [] subStrings = line.split(";;;");
                identifier = subStrings[0];

                switch(identifier){

                    //add new task list object to data
                    case "!$$":{

                        ToDoList newList = new ToDoList(subStrings[1]);     //subStrings[1] contains task list's name

                        data.add(newList);
                        currentToDoListIndex = data.indexOf(newList);

                        break;
                    }

                    //add new incomplete or complete task to the list
                    case "!--" : case "!++":{

                        //following index of subStrings array represent following information :
                        //0 = identifier, 1 = task name, 2 = task due date, 3 = task priority, 4 = task note
                        //NOTE : any index above may or may not exist

                        //variable to hold task's information loaded from file
                        //will be eventually saved into data variable
                        Task newTask = new Task(subStrings[1]);

                        if(!subStrings[2].equals("!*---*")){

                            //following index of subStrings array represent following information :
                            //index 0 = month, index 1 = day number of month, index 2 = year
                            String [] date = subStrings[2].split(",");
                            newTask.setTaskDueDate(LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1])));
                        }

                        //save task's priority
                        newTask.setPriority(Integer.parseInt(subStrings[3]));

                        if(!subStrings[4].equals("!*---*")){
                            newTask.setNote(subStrings[4]);
                        }

                        //add sub-tasks
                        line = reader.readLine();
                        subStrings = line.split(";;;");

                        //if string subStrings represents a list of sub task(s) contains information about at least one sub task
                        if(subStrings[0].equals("![]") && !subStrings[1].equals("!*---*")){

                            //marker to mark if the first index (index 0) of subStrings array is traversed in the for each loop below
                            //if index 0 is traversed, mark it true
                            boolean identifierSkipped = false;

                            for(String i : subStrings){

                                if(!identifierSkipped){
                                    identifierSkipped = true;
                                    continue;
                                }

                                newTask.getSubTasks().add(new SubTask(i));
                            }
                        }

                        if(identifier.equals("!--")){

                            //store newTask as an incomplete task
                            data.get(currentToDoListIndex).getIncompleteTaskList().add(newTask);
                        }else{

                            //store newTask as a complete task
                            data.get(currentToDoListIndex).getCompleteTaskList().add(newTask);
                        }

                        break;
                    }

                    default:
                        break;
                }
            }

            reader.close();

        }catch (IOException e){

            e.printStackTrace();        //for testing
        }finally {

            try{
                if(reader != null){
                    reader.close();
                }
            }catch(IOException e){
                e.printStackTrace();    //for testing
            }
        }
    }
}
