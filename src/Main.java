
import pages.*;
import windows.MainWindow;

public class Main {
    public static void main(String[] args) {
        /* Pages */
        MainWindow.addPage("login", new LoginPage());
        MainWindow.addPage("signup", new SignupPage());
        MainWindow.addFrame("StudentDashBoard",new StudentDashBoard());
        MainWindow.addFrame("CourseLessons",new CourseLessons());
        MainWindow.addFrame("studentlessons",new StudentLessons());
        MainWindow.addFrame("CorsesView",new CoursesView());
        /* Starting up main window */
        MainWindow.nameWindow("SkillForge");
        MainWindow.addIcon("src/resources/icon.png");
        // If resizing the window is needed, uncomment the line below
        // MainWindow.setDimensions(800, 600);
        MainWindow.goTo("login");
        MainWindow.start();
    }
}
