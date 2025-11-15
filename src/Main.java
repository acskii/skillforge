
import pages.CourseLessons;
import pages.LoginPage;
import pages.SignupPage;
import pages.StudentDashBoard;
import windows.MainWindow;

public class Main {
    public static void main(String[] args) {
        /* Pages */
        MainWindow.addPage("login", new LoginPage());
        MainWindow.addPage("signup", new SignupPage());
        MainWindow.addFrame("StudentDashBoard",new StudentDashBoard());
        MainWindow.addFrame("CourseLessons",new pages.CourseLessons());
        MainWindow.addFrame("studentlessons",new pages.StudentLessons());
        MainWindow.addFrame("CorsesView",new pages.CoursesView());
        StudentDashBoard.start(1);
        MainWindow.goToFrame("StudentDashBoard");
        /* Starting up main window */
        MainWindow.nameWindow("SkillForge");
        MainWindow.addIcon("src/resources/icon.png");
        // If resizing the window is needed, uncomment the line below
        // MainWindow.setDimensions(800, 600);
//        MainWindow.goTo("login");
//        MainWindow.start();
    }
}
