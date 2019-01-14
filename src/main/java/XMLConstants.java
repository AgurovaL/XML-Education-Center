public interface XMLConstants {
    interface Menus {
        String IMPORT_MENU_ITEM_TEXT = "Import from XML";
        String EXIT_MENU_ITEM_TEXT = "Exit";
        String FILE_MENU_TITLE = "File";
        String FILE_CHOOSER_TITLE = "Open XML File";
    }

    interface ModelNames {
        enum Models {
            STUDENT,
            PROGRAM,
            COURSE,
            TASK
        }
    }

    interface TreeTexts {
        String ROOT_ITEM_TITLE = "Students";
        String TITLE_TEXT = "Title: ";
        String AUTHOR_TEXT = "Author: ";
        String CREATION_DATE_TEXT = "Last modified: ";
        String DURATION_TEXT = "Duration (hrs): ";
        String FULL_NAME_TEXT = "Full name: ";
        String EMAIL_TEXT = "Email: ";
        String REGION_TEXT = "Region: ";
        String START_DATE_TEXT = "Start date: ";
        String TYPE_TEXT = "Type: ";
        String MARK_TEXT = "Mark: ";
        String STATUS_TEXT = "Status: ";
    }
}
