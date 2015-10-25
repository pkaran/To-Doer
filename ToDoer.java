import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.ToDoList;

import java.io.IOException;

/**
 * Starting point for application defining stage and setting properties for the stage
 *
 * @author Karan Patel
 */

public class ToDoer extends Application{

    final String PROGRAM_NAME = "To Doer";
    final String ICON_PATH = "media/icon.jpg";
    final String FXML_SCENE_HIERARCHY_PATH = "TodoView.fxml";

    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SCENE_HIERARCHY_PATH));
        ScrollPane scene_root = loader.load();
        ToDoController controller = loader.getController();
        ObservableList<ToDoList> data = controller.getData();

        Image primaryStage_icon = new Image(ICON_PATH);
        primaryStage.getIcons().add(primaryStage_icon);
        primaryStage.setScene(new Scene(scene_root));
        primaryStage.setTitle(PROGRAM_NAME);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String [] args){
        launch(args);
    }

}
