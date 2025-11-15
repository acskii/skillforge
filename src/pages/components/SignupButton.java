package pages.components;

import databases.UserDatabase;
import windows.MainWindow;

import javax.swing.*;
import java.awt.*;

// Andrew :)

public class SignupButton extends JButton {
    private final JTextField name;
    private final JTextField email;
    private final JPasswordField password;
    private final JComboBox<String> role;
    private final UserDatabase db = UserDatabase.getInstance();

    public SignupButton(JTextField name, JTextField email, JPasswordField password, JComboBox<String> role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;

        /* Design Signup Button */
        setText("Sign Up");
        setPreferredSize(new Dimension(200, 30));
        setBackground(new Color(30, 203, 225));
        setForeground(Color.white);
        setFont(new Font("Arial", Font.BOLD, 16));
        setBorder(BorderFactory.createLineBorder(Color.black, 2, true));

        /* On click event listener */
        addActionListener((e) -> verifySignup());

        /* Disable button if not initialised correctly */
        if (name == null || email == null || password == null || role == null) {
            System.out.println("[SignupButton]: Must provide all required fields\n");
            setEnabled(false);
        }
    }

    private void verifySignup() {
        /* Get form values */
        String userName = name.getText().trim();
        String userEmail = email.getText().trim();

        StringBuilder pwdSb = new StringBuilder();
        for (char c : password.getPassword()) pwdSb.append(c);
        String userPassword = pwdSb.toString();

        String userRole = (String) role.getSelectedItem();

        /* Validate inputs */
        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        /* Validate email format */
        //  TODO: Validation is needed here
        //            JOptionPane.showMessageDialog(this,
        //                    "Please enter a valid email address",
        //                    "Invalid Email",
        //                    JOptionPane.WARNING_MESSAGE);
        //            return;

        /* Validate password length */
        if (userPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        /* Attempt to add user */
        try {
            boolean success = db.addUser(userName, userEmail, userPassword, userRole);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully! Please login.",
                        "Signup Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                /* Clear fields and go to login */
                name.setText("");
                email.setText("");
                password.setText("");
                role.setSelectedIndex(0);
                /* Redirect to login */
                MainWindow.goTo("login");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Email already exists. Please use a different email.",
                        "Signup Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred during signup. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("[SignupButton]: Error during signup - " + e.getMessage());
        }
    }
}