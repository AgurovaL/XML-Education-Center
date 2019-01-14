package models;

import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class Report extends TreeItem implements ReportModels {
    private int studentID;
    private int programID;
    private int courseID;
    private int taskID;

    private int mark;
    private String status;
}
