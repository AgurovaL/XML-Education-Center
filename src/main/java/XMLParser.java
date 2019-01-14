import lombok.Data;
import models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class XMLParser {
    private String filePath;
    private Document document;

    private final List<Report> reportList = new ArrayList<>();
    private final List<Student> studentList = new ArrayList<>();
    private final List<Program> programList = new ArrayList<>();
    private final List<Course> courseList = new ArrayList<>();
    private final List<Task> taskList = new ArrayList<>();

    private final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

    public XMLParser(String filePath) {
        this.filePath = filePath;
    }

    void parseXML() {
        try {
            File inputFile = new File(filePath);

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(inputFile);

            //parse every object from Models
            bindComponents();

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void bindComponents() {
        parseReports();
        parseStudents();
        parsePrograms();
        parseCourses();
        parseTasks();
    }

    private void parseReports() {
        NodeList nodeListReports = document.getElementsByTagName("report");
        for (int i = 0; i < nodeListReports.getLength(); i++) {
            Node node = nodeListReports.item(i);
            Report report = new Report();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                report.setStudentID(Integer.parseInt(element.getElementsByTagName("studentID").item(0).getTextContent()));
                report.setProgramID(Integer.parseInt(element.getElementsByTagName("programID").item(0).getTextContent()));
                report.setCourseID(Integer.parseInt(element.getElementsByTagName("courseID").item(0).getTextContent()));
                report.setTaskID(Integer.parseInt(element.getElementsByTagName("taskID").item(0).getTextContent()));
                report.setMark(Integer.parseInt(element.getElementsByTagName("mark").item(0).getTextContent()));
                report.setStatus(element.getElementsByTagName("status").item(0).getTextContent());

                reportList.add(report);
            }
        }
    }

    private void parseStudents() {
        NodeList nodeListStudents = document.getElementsByTagName("student");
        for (int i = 0; i < nodeListStudents.getLength(); i++) {
            Node node = nodeListStudents.item(i);
            Student student = new Student();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                student.setId(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
                student.setName(element.getElementsByTagName("name").item(0).getTextContent());
                student.setCity(element.getElementsByTagName("city").item(0).getTextContent());
                student.setEmail(element.getElementsByTagName("email").item(0).getTextContent());
                try {
                    student.setStartDate(dateFormat.parse(element.getElementsByTagName("start_date").item(0).getTextContent()));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                student.setHasContract(Boolean.parseBoolean(element.getElementsByTagName("has_contract").item(0).getTextContent()));
                student.setProgramID(Integer.parseInt(element.getElementsByTagName("programID").item(0).getTextContent()));

                studentList.add(student);
            }
        }
    }

    private void parsePrograms() {
        NodeList nodeListPrograms = document.getElementsByTagName("program");
        for (int i = 0; i < nodeListPrograms.getLength(); i++) {
            Node node = nodeListPrograms.item(i);
            Program program = new Program();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                program.setId(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
                program.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
                program.setAuthor(element.getElementsByTagName("author").item(0).getTextContent());
                try {
                    program.setCreationDate(dateFormat.parse(element.getElementsByTagName("creation_date").item(0).getTextContent()));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                NodeList nodeListCourses = ((Element) node).getElementsByTagName("courseID");
                program.setCourses(new int[nodeListCourses.getLength()]);
                for (int j = 0; j < nodeListCourses.getLength(); j++) {
                    program.getCourses()[j] = Integer.parseInt(nodeListCourses.item(j).getTextContent());
                }

                programList.add(program);
            }
        }
    }

    private void parseCourses() {
        NodeList nodeListCourses = document.getElementsByTagName("course");
        for (int i = 0; i < nodeListCourses.getLength(); i++) {
            Node node = nodeListCourses.item(i);
            Course course = new Course();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                course.setId(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
                course.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
                course.setAuthor(element.getElementsByTagName("author").item(0).getTextContent());
                try {
                    course.setCreationDate(dateFormat.parse(element.getElementsByTagName("creation_date").item(0).getTextContent()));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                NodeList nodeListTasks = ((Element) node).getElementsByTagName("taskID");
                course.setTasks(new int[nodeListTasks.getLength()]);
                for (int j = 0; j < nodeListTasks.getLength(); j++) {
                    course.getTasks()[j] = Integer.parseInt(nodeListTasks.item(j).getTextContent());
                }

                courseList.add(course);
            }
        }
    }

    private void parseTasks() {
        NodeList nodeListTasks = document.getElementsByTagName("task");
        for (int i = 0; i < nodeListTasks.getLength(); i++) {
            Node node = nodeListTasks.item(i);
            Task task = new Task();

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                task.setId(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
                task.setType(element.getElementsByTagName("type").item(0).getTextContent());
                task.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
                task.setHours(Integer.parseInt(element.getElementsByTagName("hours").item(0).getTextContent()));

                taskList.add(task);
            }
        }
    }
}