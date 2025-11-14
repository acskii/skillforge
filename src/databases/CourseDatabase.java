package databases;

import models.Course;
import models.Lesson;

import java.util.List;

// Andrew :)

public class CourseDatabase extends Database<Course> {
    private int lessonIndex = 1;

    public CourseDatabase(String filename) {
        super(filename, Course.class);
        this.logName = "CourseDatabase";


        /* Determines the most recent ID for all lessons */
        for (Course c : this.records) {
            for (Lesson l : c.getLessons()) {
                this.lessonIndex = Math.max(l.getId(), this.lessonIndex);
            }
        }
    }

    public void deleteCourse(int id) {deleteRecord(id);}
    public Course getCourseById(int id) {return getRecordById(id);}

    public void addCourse(String title, int instructorId, List<Lesson> lessons) {
        Course course = new Course();
        course.setId(++this.index);
        course.setLessons(lessons);
        course.setTitle(title);
        course.setInstructorId(instructorId);

        insertRecord(course);
    }

    public void addLesson(int courseId, String title, String content) {
        /* Insert new lesson into an existing course */
        Lesson lesson = new Lesson();
        lesson.setContent(content);
        lesson.setTitle(title);
        lesson.setId(++this.lessonIndex);

        Course course = getRecordById(courseId);
        List<Lesson> newLessons = course.getLessons();
        newLessons.add(lesson);

        updateCourse(courseId, course.getTitle(), newLessons);
    }

    public void updateCourse(int id, String title, List<Lesson> lessons) {
        Course course = getRecordById(id);
        if (course == null) {
            System.out.printf("[%s]: User attempting to update doesn't exist [ID] %d\n", logName, id);
            return;
        }

        /* Change filters */
        if (!course.getTitle().equals(title)) course.setTitle(title);
        if (lessons != null && !lessons.equals(course.getLessons())) course.setLessons(lessons);

        /* Remove the old one */
        deleteRecord(id);
        /* Add the changed one */
        insertRecord(course);
    }

    // TODO: update lesson with student progress
}
