package services;

import databases.CourseDatabase;
import databases.UserDatabase;
import models.Certificate;
import models.Course;
import models.Lesson;
import models.Progress;
import models.QuizAttempt;
import models.Student;
import services.CourseService;
import services.InstructorService;
import services.StudentService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CertificateService {
    private static final CourseDatabase courseDb = CourseDatabase.getInstance();
    private static final UserDatabase userDb = UserDatabase.getInstance();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    /**
     * Generates a PDF certificate for a student who completed a course
     * @param studentId The student's ID
     * @param courseId The course ID
     * @return The file path of the generated PDF certificate, or null if generation fails
     */
    public static String generateCertificatePDF(int studentId, int courseId) {
        Student student = StudentService.getStudent(studentId);
        Course course = courseDb.getCourseById(courseId);
        CourseService courseService = new CourseService();
        Certificate certificate = courseService.getCertificate(courseId, studentId);

        if (student == null || course == null) {
            return null;
        }

        // If certificate doesn't exist but course is complete, create it
        if (certificate == null && CourseService.isComplete(courseId, studentId)) {
            courseService.createCertificate(courseId, studentId);
            // Refresh student to get updated certificate
            student = StudentService.getStudent(studentId);
            certificate = courseService.getCertificate(courseId, studentId);
        }

        if (certificate == null) {
            return null;
        }

        // Create certificates directory if it doesn't exist
        File certDir = new File("certificates");
        if (!certDir.exists()) {
            certDir.mkdirs();
        }

        // Get the last quiz attempt date from the last lesson with a quiz
        LocalDateTime lastAttemptDate = getLastQuizAttemptDate(course, studentId);
        if (lastAttemptDate == null) {
            // Fallback to certificate issue date if no quiz attempt found
            lastAttemptDate = certificate.getIssued().atStartOfDay();
        }
        
        // Get instructor name
        String instructorName = InstructorService.getInstructor(course.getInstructorId()).getName();

        // Generate safe filename
        String safeStudentName = sanitizeFilename(student.getName());
        String safeCourseTitle = sanitizeFilename(course.getTitle());
        String issueDate = lastAttemptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String certCode = generateCertificateCode(certificate.getId(), studentId, courseId);

        String filename = String.format("certificate_%s_%s_%s_%s.pdf",
            safeStudentName, safeCourseTitle, issueDate, certCode);
        String filepath = new File(certDir, filename).getAbsolutePath();

        try (PDDocument document = new PDDocument()) {
            // Create landscape page (11x8.5 inches)
            PDPage page = new PDPage(new PDRectangle(792, 612)); // 11x8.5 inches at 72 DPI
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Background color (light blue)
                contentStream.setNonStrokingColor(240, 248, 255);
                contentStream.addRect(0, 0, 792, 612);
                contentStream.fill();

                // Border (gold)
                contentStream.setStrokingColor(218, 165, 32);
                contentStream.setLineWidth(10);
                contentStream.addRect(30, 30, 732, 552);
                contentStream.stroke();

                // Inner border (lighter gold)
                contentStream.setStrokingColor(255, 215, 0);
                contentStream.setLineWidth(3);
                contentStream.addRect(50, 50, 692, 512);
                contentStream.stroke();

                // Watermark text (subtle)
                contentStream.setNonStrokingColor(200, 200, 200);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 60);
                contentStream.newLineAtOffset(396, 306);
                contentStream.showText("CERTIFICATE OF COMPLETION");
                contentStream.endText();
                
                // Main Title
                contentStream.setNonStrokingColor(0, 0, 128);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 48);
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Certificate of Completion") / 1000 * 48;
                contentStream.newLineAtOffset((792 - titleWidth) / 2, 480);
                contentStream.showText("Certificate of Completion");
                contentStream.endText();
                
                // This is to certify text
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 16);
                String certText = "This is to certify that";
                float certTextWidth = PDType1Font.HELVETICA.getStringWidth(certText) / 1000 * 16;
                contentStream.newLineAtOffset((792 - certTextWidth) / 2, 380);
                contentStream.showText(certText);
                contentStream.endText();
                
                // Student Name (prominent)
                contentStream.setNonStrokingColor(0, 51, 102);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 36);
                String studentName = student.getName();
                float nameWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(studentName) / 1000 * 36;
                contentStream.newLineAtOffset((792 - nameWidth) / 2, 320);
                contentStream.showText(studentName);
                contentStream.endText();
                
                // Has successfully completed text
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 16);
                String completedText = "has successfully completed";
                float completedTextWidth = PDType1Font.HELVETICA.getStringWidth(completedText) / 1000 * 16;
                contentStream.newLineAtOffset((792 - completedTextWidth) / 2, 260);
                contentStream.showText(completedText);
                contentStream.endText();
                
                // Course Title
                contentStream.setNonStrokingColor(0, 102, 204);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 28);
                String courseTitle = course.getTitle();
                float courseTitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(courseTitle) / 1000 * 28;
                // If course title is too long, wrap it
                if (courseTitleWidth > 650) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                    courseTitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(courseTitle) / 1000 * 20;
                }
                contentStream.newLineAtOffset((792 - courseTitleWidth) / 2, 210);
                contentStream.showText(courseTitle);
                contentStream.endText();
                
                // Details block
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                String issueDateText = "Issued on: " + lastAttemptDate.format(DATE_FORMATTER);
                float issueDateWidth = PDType1Font.HELVETICA.getStringWidth(issueDateText) / 1000 * 12;
                contentStream.newLineAtOffset((792 - issueDateWidth) / 2, 150);
                contentStream.showText(issueDateText);
                contentStream.endText();
                
                // Certificate Code
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                String codeText = "Certificate Code: " + certCode;
                float codeWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(codeText) / 1000 * 10;
                contentStream.newLineAtOffset((792 - codeWidth) / 2, 120);
                contentStream.showText(codeText);
                contentStream.endText();
                
                // Signature lines
                contentStream.setNonStrokingColor(0, 0, 0);
                contentStream.setLineWidth(1);
                contentStream.moveTo(150, 80);
                contentStream.lineTo(300, 80);
                contentStream.stroke();
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(150, 65);
                contentStream.showText("Instructor Signature");
                contentStream.endText();
                
                // Instructor Name (centered under signature line from 150 to 300)
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                float instructorNameWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(instructorName) / 1000 * 14;
                // Center of signature line is at 225 (middle of 150-300)
                float instructorX = 225 - (instructorNameWidth / 2);
                contentStream.newLineAtOffset(instructorX, 50);
                contentStream.showText(instructorName);
                contentStream.endText();
                
                contentStream.moveTo(492, 80);
                contentStream.lineTo(642, 80);
                contentStream.stroke();
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(492, 65);
                contentStream.showText("Date");
                contentStream.endText();
            }

            document.save(filepath);
            return filepath;

        } catch (IOException e) {
            System.err.println("Error generating certificate PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Downloads the certificate PDF by triggering a file download
     * @param filepath The path to the PDF file
     * @return true if download was successful, false otherwise
     */
    public static boolean downloadCertificate(String filepath) {
        if (filepath == null || !new File(filepath).exists()) {
            return false;
        }

        try {
            File file = new File(filepath);
            java.awt.Desktop.getDesktop().open(file);
            return true;
        } catch (IOException e) {
            System.err.println("Error opening certificate file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a unique certificate code
     */
    private static String generateCertificateCode(int certId, int studentId, int courseId) {
        return String.format("CERT-%04d-%04d-%04d", certId, studentId, courseId);
    }

    /**
     * Sanitizes a filename by removing invalid characters
     */
    private static String sanitizeFilename(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9\\s-_]", "").replaceAll("\\s+", "_").substring(0, Math.min(name.length(), 30));
    }

    /**
     * Checks if a student has completed a course
     */
    public static boolean isCourseCompleted(int studentId, int courseId) {
        return CourseService.isComplete(courseId, studentId);
    }

    /**
     * Gets or creates a certificate for a student and course
     */
    public static Certificate getOrCreateCertificate(int studentId, int courseId) {
        CourseService courseService = new CourseService();
        Certificate cert = courseService.getCertificate(courseId, studentId);

        if (cert == null && isCourseCompleted(studentId, courseId)) {
            courseService.createCertificate(courseId, studentId);
            // Refresh student to get updated certificate
            Student student = StudentService.getStudent(studentId);
            if (student.getCertificates() != null) {
                for (Certificate c : student.getCertificates()) {
                    if (c.getCourseId() == courseId) {
                        return c;
                    }
                }
            }
        }

        return cert;
    }
    
    /**
     * Gets the date of the last quiz attempt from the last lesson with a quiz in the course
     * @param course The course
     * @param studentId The student's ID
     * @return The LocalDateTime of the last quiz attempt, or null if no quiz attempts found
     */
    private static LocalDateTime getLastQuizAttemptDate(Course course, int studentId) {
        if (course == null || course.getLessons() == null || course.getLessons().isEmpty()) {
            return null;
        }
        
        List<Lesson> lessons = course.getLessons();
        
        // Find the last lesson (highest ID) with a quiz that has attempts
        Lesson lastLessonWithQuiz = null;
        for (int i = lessons.size() - 1; i >= 0; i--) {
            Lesson lesson = lessons.get(i);
            if (lesson.getQuiz() != null) {
                Progress progress = lesson.getStudentProgress().get(studentId);
                if (progress != null && progress.getAttempts() != null && !progress.getAttempts().isEmpty()) {
                    lastLessonWithQuiz = lesson;
                    break; // Found the last lesson with a quiz and attempts
                }
            }
        }
        
        if (lastLessonWithQuiz == null) {
            return null;
        }
        
        // Get the last attempt from this last lesson
        Progress progress = lastLessonWithQuiz.getStudentProgress().get(studentId);
        if (progress == null || progress.getAttempts() == null || progress.getAttempts().isEmpty()) {
            return null;
        }
        
        List<QuizAttempt> attempts = progress.getAttempts();
        QuizAttempt lastAttempt = attempts.get(attempts.size() - 1);
        
        // Return finishTime if available, otherwise startTime
        return lastAttempt.getFinishTime() != null ? 
            lastAttempt.getFinishTime() : lastAttempt.getStartTime();
    }
}

