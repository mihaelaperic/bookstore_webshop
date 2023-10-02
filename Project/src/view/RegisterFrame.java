package view;

import controller.CartController;
import controller.UserController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The `RegisterFrame` class provides a graphical user interface (GUI) for new users to register within the bookstore application.
 * <p>
 * It captures essential user information such as username, password, and email. After entering the required details, users can click the "Register" button to initiate the registration process. Upon successful registration, users are directed to the login screen, and an affirmation message is displayed.
 * <p>
 * In case of registration failure due to invalid or duplicate inputs, error messages guide users to correct their entries. The class incorporates validation methods to ensure proper email format and that all fields are populated.
 */
public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton registerButton;
    private JPanel registerPanel;

    public RegisterFrame() {
        super("Register");
        setSize(400, 250);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initAll();
        layoutAll();
        activateApp();

    }

    private void initAll() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);
        registerPanel = new JPanel();
        registerButton = new JButton("Register");
    }

    private void layoutAll() {
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10);


        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        registerPanel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        registerPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        registerPanel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        registerPanel.add(passwordField, constraints);

        JLabel emailLabel = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        registerPanel.add(emailLabel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        registerPanel.add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
//        constraints.weighty = 0.01;
        registerPanel.add(registerButton, constraints);
        add(registerPanel, BorderLayout.CENTER);


    }

    private void activateApp() {
        UserController userController = new UserController(new CartController());
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String email = emailField.getText();

                if (isValidRegistration(username, password, email)) {
                    boolean success = userController.registerUser(username, email, new String(password), "customer");

                    if(success) {
                        new LoginFrame();
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Registration successful!");
                        dispose();

                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Registration failed. Try a different username/email.");
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Registration failed. Please check your input.");
                }

                usernameField.setText("");
                passwordField.setText("");
                emailField.setText("");
            }
        });
    }



    private boolean isValidRegistration(String username, char[] password, String email) {
        return !username.isEmpty() && password.length > 0 && isValidEmail(email);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

}
