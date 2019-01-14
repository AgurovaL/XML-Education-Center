package models;

import javafx.scene.control.TreeItem;
import lombok.Data;

@Data
public class Task extends TreeItem implements ReportModels {
    private int id;
    private String type;
    private String title;
    private int hours;

    @Override
    public String toString() {
        return this.title;
    }
}
