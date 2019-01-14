package models;

import javafx.scene.control.TreeItem;
import lombok.Data;

import java.util.Date;

@Data
public class Student extends TreeItem implements ReportModels {
    private int id;
    private String name;
    private String city;
    private String email;
    private Date startDate;
    private boolean hasContract;

    private int programID;

    @Override
    public String toString() {
        return this.name;
    }
}
