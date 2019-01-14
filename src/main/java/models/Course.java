package models;

import javafx.scene.control.TreeItem;
import lombok.Data;

import java.util.Date;

@Data
public class Course extends TreeItem implements ReportModels {
    private int id;
    private String title;
    private String author;
    private Date creationDate;
    private int[] tasks;

    @Override
    public String toString() {
        return this.title;
    }
}
