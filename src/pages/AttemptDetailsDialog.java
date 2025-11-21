package pages;

import databases.CourseDatabase;
import models.Lesson;
import models.Question;
import models.Quiz;
import models.QuizAttempt;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AttemptDetailsDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AttemptDetailsDialog(JDialog parent, Lesson lesson, QuizAttempt attempt, int studentId) {
        super(parent, "Quiz Attempt Details", true);
        
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Quiz quiz = lesson.getQuiz();
        if (quiz == null) {
            JOptionPane.showMessageDialog(this, "Quiz not found.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Header info
        JPanel headerPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Attempt Summary"));
        
        String dateTime = attempt.getStartTime() != null 
            ? attempt.getStartTime().format(DATE_FORMATTER)
            : "N/A";
        String finishTime = attempt.getFinishTime() != null 
            ? attempt.getFinishTime().format(DATE_FORMATTER)
            : "N/A";
        String status = attempt.getStatus() != null ? attempt.getStatus() : "unknown";
        String score = String.format("%.1f%%", attempt.getScore());
        int correct = attempt.getCorrectQuestions();
        int wrong = attempt.getWrongQuestions();
        
        headerPanel.add(new JLabel("Start Time:"));
        headerPanel.add(new JLabel(dateTime));
        headerPanel.add(new JLabel("Finish Time:"));
        headerPanel.add(new JLabel(finishTime));
        headerPanel.add(new JLabel("Status:"));
        headerPanel.add(new JLabel(status));
        headerPanel.add(new JLabel("Score:"));
        JLabel scoreLabel = new JLabel(score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        if (attempt.isPassed()) {
            scoreLabel.setForeground(Color.GREEN);
        } else {
            scoreLabel.setForeground(Color.RED);
        }
        headerPanel.add(scoreLabel);
        headerPanel.add(new JLabel("Correct Answers:"));
        JLabel correctLabel = new JLabel(String.valueOf(correct));
        correctLabel.setForeground(Color.GREEN);
        headerPanel.add(correctLabel);
        headerPanel.add(new JLabel("Wrong Answers:"));
        JLabel wrongLabel = new JLabel(String.valueOf(wrong));
        wrongLabel.setForeground(Color.RED);
        headerPanel.add(wrongLabel);
        
        // Questions panel
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBorder(BorderFactory.createTitledBorder("Questions & Answers"));
        
        Map<Integer, Integer> studentAnswers = attempt.getQuestionAnswers();
        List<Question> questions = quiz.getQuestions();
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Integer studentAnswerIndex = studentAnswers != null ? studentAnswers.get(i) : null;
            
            // Find correct answer index
            int correctAnswerIndex = -1;
            for (int j = 0; j < question.getChoices().size(); j++) {
                if (question.getChoices().get(j).isCorrect()) {
                    correctAnswerIndex = j;
                    break;
                }
            }
            
            boolean isCorrect = studentAnswerIndex != null && studentAnswerIndex == correctAnswerIndex;
            
            JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isCorrect ? Color.GREEN : Color.RED, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            questionPanel.setBackground(isCorrect ? new Color(230, 255, 230) : new Color(255, 230, 230));
            
            // Question header
            JLabel questionLabel = new JLabel("<html><b>Question " + (i + 1) + ":</b> " + question.getHeader() + "</html>");
            questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
            questionPanel.add(questionLabel, BorderLayout.NORTH);
            
            // Choices
            JPanel choicesPanel = new JPanel();
            choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
            
            for (int j = 0; j < question.getChoices().size(); j++) {
                Question.Choice choice = question.getChoices().get(j);
                JLabel choiceLabel = new JLabel("<html>" + (char)('A' + j) + ") " + choice.getText() + "</html>");
                
                if (j == correctAnswerIndex) {
                    choiceLabel.setForeground(Color.GREEN);
                    choiceLabel.setText(choiceLabel.getText() + " ✓ (Correct)");
                } else if (studentAnswerIndex != null && j == studentAnswerIndex) {
                    choiceLabel.setForeground(Color.RED);
                    choiceLabel.setText(choiceLabel.getText() + " ✗ (Your Answer)");
                } else {
                    choiceLabel.setForeground(Color.BLACK);
                }
                
                choiceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                choiceLabel.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
                choicesPanel.add(choiceLabel);
            }
            
            if (studentAnswerIndex == null) {
                JLabel noAnswerLabel = new JLabel("No answer provided");
                noAnswerLabel.setForeground(Color.ORANGE);
                noAnswerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                choicesPanel.add(noAnswerLabel);
            }
            
            questionPanel.add(choicesPanel, BorderLayout.CENTER);
            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeBtn);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
}

