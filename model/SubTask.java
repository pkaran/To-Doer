package model;

/**
 * Definition of SubTask class
 *
 * @author Karan Patel
 */

public class SubTask {

    private String title;                   //title / name of the sub-task

    public SubTask(){
        title = "Empty sub-task";
    }

    public SubTask(String title){
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }
}
