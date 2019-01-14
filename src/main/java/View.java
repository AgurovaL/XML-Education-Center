import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class View extends Stage {
    private List<Report> reportList;
    private List<Student> studentList;
    private List<Program> programList;
    private List<Course> courseList;
    private List<Task> taskList;

    private MenuBar menuBar;
    private TreeView<ReportModels> tree;
    private TextArea textArea;
    private Alert alert;

    private final Image folderIcon = new Image(getClass().getResourceAsStream("/folder-icon.png"));
    private final Image documentIcon = new Image(getClass().getResourceAsStream("/document-icon.png"));
    private final Image computerIcon = new Image(getClass().getResourceAsStream("/computer-icon.png"));

    private enum Model {Studemt, Program, Course, Task}


    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");

    public View() {
        super();
        setSettings();
    }

    private void setSettings() {
        setTitle("Curriculum Viewer");
        getIcons().add(computerIcon);
        setWidth(600);
        setHeight(500);
        setMaxHeight(600);
        setMaxWidth(600);
    }

    void initComponents() {
        createMenu();
        createTree();
        createTextArea();

        //create grid panel with components
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setPadding(new Insets(10));
        grid.add(menuBar, 0, 0);
        grid.add(tree, 0, 1);
        grid.add(textArea, 1, 1);

        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        Scene scene = new Scene(grid);
        setScene(scene);

        show();
    }

    private void createMenu() {
        menuBar = new MenuBar();
        Menu fileMenu = new Menu(XMLConstants.Menus.FILE_MENU_TITLE);

        MenuItem importItem = new MenuItem(XMLConstants.Menus.IMPORT_MENU_ITEM_TEXT);
        MenuItem exitItem = new MenuItem(XMLConstants.Menus.EXIT_MENU_ITEM_TEXT);

        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        exitItem.setOnAction((ActionEvent event) ->
        {
            System.exit(0);
        });

        importItem.setOnAction((ActionEvent event) -> {
            importFromXML();
        });

        fileMenu.getItems().addAll(importItem, exitItem);
        menuBar.getMenus().addAll(fileMenu);
    }

    private void importFromXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(XMLConstants.Menus.FILE_CHOOSER_TITLE);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        try {
            File file = fileChooser.showOpenDialog(new Stage());
            //get filePath of the XML file and send it to the parser
            String path = file.getAbsolutePath();
            XMLParser xmlParser = new XMLParser(path);
            xmlParser.parseXML();

            //get all information from the parser
            reportList = xmlParser.getReportList();
            studentList = xmlParser.getStudentList();
            programList = xmlParser.getProgramList();
            courseList = xmlParser.getCourseList();
            taskList = xmlParser.getTaskList();

            fillTree();
            createTreeListener();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            alert.setContentText("File doen't exist!");
            alert.showAndWait();
        }
    }

    private void createTree() {
        Student student = new Student();
        student.setName(XMLConstants.TreeTexts.ROOT_ITEM_TITLE);
        TreeItem<ReportModels> rootItem = new TreeItem<>(student, new ImageView(folderIcon));
        rootItem.setExpanded(true);

        tree = new TreeView<>(rootItem);
    }

    private void fillTree() {
        TreeItem<ReportModels> rootItem = tree.getRoot();

        for (Student student : studentList) {
            //CREATE item - student
            TreeItem<ReportModels> studentItem = new TreeItem<ReportModels>(student, new ImageView(folderIcon));

            //CREATE item - program
            int programID = student.getProgramID();
            for (Program program : programList) {
                if (program.getId() == programID) {
                    TreeItem<ReportModels> programItem = new TreeItem<ReportModels>(program, new ImageView(folderIcon));

                    //CREATE item - course
                    for (int courseID : program.getCourses()) {
                        for (Course course : courseList) {
                            if (course.getId() == courseID) {
                                TreeItem<ReportModels> courseItem = new TreeItem<ReportModels>(course, new ImageView(folderIcon));

                                //CREATE item - task
                                for (int taskID : course.getTasks()) {
                                    for (Task task : taskList) {
                                        if (task.getId() == taskID) {
                                            TreeItem<ReportModels> taskItem = new TreeItem<ReportModels>(task, new ImageView(documentIcon));
                                            //ADD item - task
                                            courseItem.getChildren().add(taskItem);
                                        }
                                    }
                                }
                                //ADD item - course
                                programItem.getChildren().add(courseItem);
                            }
                        }
                    }
                    //ADD item - program
                    studentItem.getChildren().add(programItem);
                }
            }
            //ADD item - student
            rootItem.getChildren().add(studentItem);
        }
    }

    private void createTreeListener() {
        tree
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Class itemClass = observable
                            .getValue()
                            .getValue()
                            .getClass();
                    switch (XMLConstants.ModelNames.Models.valueOf(itemClass.getSimpleName().toUpperCase())) {
                        case STUDENT:
                            createStudentText((Student) observable.getValue().getValue());
                            break;
                        case PROGRAM:
                            createProgramText((Program) observable.getValue().getValue());
                            break;
                        case COURSE:
                            createCourseText((Course) observable.getValue().getValue());
                            break;
                        case TASK:
                            Student student = (Student) observable
                                    .getValue()
                                    .getParent()
                                    .getParent()
                                    .getParent()
                                    .getValue();
                            int studentId = student.getId();
                            createTaskText((Task) observable.getValue().getValue(), studentId);
                            break;
                    }
                });
    }

    private void createTextArea() {
        textArea = new TextArea();
        textArea.setPrefColumnCount(25);
        textArea.setPrefRowCount(5);
    }

    private void createStudentText(Student student) {
        textArea.clear();
        if (!student.getName().equals(XMLConstants.TreeTexts.ROOT_ITEM_TITLE)) {
            textArea.appendText(XMLConstants.TreeTexts.FULL_NAME_TEXT + student.getName() + "\n");
            textArea.appendText(XMLConstants.TreeTexts.EMAIL_TEXT + student.getEmail() + "\n");
            textArea.appendText(XMLConstants.TreeTexts.REGION_TEXT + student.getCity() + "\n");
            if (student.isHasContract()) {
                textArea.appendText("Contract Signed: yes" + "\n");
            } else {
                textArea.appendText("Contract Signed: no" + "\n");
            }
            textArea.appendText(XMLConstants.TreeTexts.START_DATE_TEXT + dateFormat.format(student.getStartDate()) + "\n");
        }
    }

    private void createProgramText(Program program) {
        textArea.clear();
        textArea.appendText(XMLConstants.TreeTexts.TITLE_TEXT + program.getTitle() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.AUTHOR_TEXT + program.getAuthor() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.CREATION_DATE_TEXT + dateFormat.format(program.getCreationDate()) + "\n");

        int duration = 0;
        for (Report report : reportList) {
            if (report.getProgramID() == program.getId()) {
                duration += taskList.get(report.getTaskID() - 1).getHours();
            }
        }
        textArea.appendText(XMLConstants.TreeTexts.DURATION_TEXT + duration + "\n");

        textArea.appendText("\n" + "Courses:" + "\n");
        for (int courseID : program.getCourses()) {
            for (Course course : courseList) {
                if (courseID == course.getId()) {
                    textArea.appendText(course.getTitle() + "\n");
                }
            }
        }
    }

    private void createCourseText(Course course) {
        textArea.clear();
        textArea.appendText(XMLConstants.TreeTexts.TITLE_TEXT + course.getTitle() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.AUTHOR_TEXT + course.getAuthor() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.CREATION_DATE_TEXT + dateFormat.format(course.getCreationDate()) + "\n");

        String taskTitle = "";
        int duration = 0;
        for (int taskID : course.getTasks()) {
            duration += taskList.get(taskID - 1).getHours();
        }

        textArea.appendText(XMLConstants.TreeTexts.DURATION_TEXT + duration + "\n");

        textArea.appendText("\n" + "Tasks:" + "\n");
        for (int taskID : course.getTasks()) {
            taskTitle = taskList.get(taskID).getTitle();
            textArea.appendText(taskTitle + "\n");
        }
    }

    private void createTaskText(Task task, int studentID) {
        textArea.clear();
        textArea.appendText(XMLConstants.TreeTexts.TITLE_TEXT + task.getTitle() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.DURATION_TEXT + task.getHours() + "\n");
        textArea.appendText(XMLConstants.TreeTexts.TYPE_TEXT + task.getType() + "\n");

        for (Report report : reportList) {
            if (task.getId() == report.getTaskID() && studentID == report.getStudentID()) {
                textArea.appendText(XMLConstants.TreeTexts.MARK_TEXT + report.getMark() + "\n");
                textArea.appendText(XMLConstants.TreeTexts.STATUS_TEXT + report.getStatus() + "\n");
            }
        }
    }
}
