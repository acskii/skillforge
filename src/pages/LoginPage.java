package pages;

import pages.components.LoginButton;
import windows.MainWindow;

import javax.swing.*;
import java.awt.*;

// Andrew :)

public class LoginPage extends JPanel {
    public LoginPage() {
        /* Set background to gray */
        setBackground(new Color(224, 225, 223));
        /* Set Layout */
        /* Allows centering without automatically filling all available space */
        setLayout(new GridBagLayout());

        /* White panel */
        JPanel whitePanel = new JPanel();
        whitePanel.setLayout(new BorderLayout());
        whitePanel.setBackground(Color.white);
        whitePanel.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
        whitePanel.setPreferredSize(new Dimension(300, 350));

        /* Form panel */
        JPanel form = new JPanel();
        form.setBackground(Color.white);
        form.setLayout(new GridBagLayout());
        form.setSize(200, 400);

        /* Setting up grid placement */
        GridBagConstraints constraints = new GridBagConstraints();

        /* Main title */
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 20, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        form.add(titleLabel, constraints);

        /* Add padding of 10px in all directions */
        constraints.insets = new Insets(10, 10, 0, 10);

        /* Email label */
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        JLabel emailLabel = new JLabel("Email");
        form.add(emailLabel, constraints);

        /* Email field */
        JTextField emailInput = new JTextField();
        emailInput.setPreferredSize(new Dimension(200, 30));
        emailInput.setMargin(new Insets(5, 5, 5, 5));
        constraints.gridy = 2;
        form.add(emailInput, constraints);

        /* Password label */
        JLabel pwdLabel = new JLabel("Password");
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_START;
        form.add(pwdLabel, constraints);

        /* Password field */
        JPasswordField pwdInput = new JPasswordField();
        pwdInput.setPreferredSize(new Dimension(200, 30));
        pwdInput.setMargin(new Insets(5, 5, 5, 5));
        constraints.gridy = 4;
        form.add(pwdInput, constraints);

        /* Login button */
        JButton loginBtn = new LoginButton(emailInput, pwdInput);
        constraints.gridy = 5;
        constraints.insets = new Insets(30, 10, 0, 10);
        form.add(loginBtn, constraints);

        /* Sign up link */
        constraints.gridy = 6;
        constraints.insets = new Insets(20, 10, 10, 10);
        JLabel signupLabel = new JLabel("Don't have an account? Sign up");
        signupLabel.setForeground(Color.BLUE);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                        MainWindow.goTo("signup");
                }
        });
        form.add(signupLabel, constraints);

        /* Add form */
        whitePanel.add(form, BorderLayout.CENTER);

        /* Add panel */
        add(whitePanel);
    }
}
