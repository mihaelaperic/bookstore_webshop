package view;

import controller.CartController;
import controller.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

/**
 * The `LoginFrame` class provides a graphical user interface (GUI) for the login feature of the bookstore application.
 * Users can input their username and password to authenticate themselves.
 * <p>
 * The frame also supports a registration option for new users.
 * On successful login, the user is directed either to the administrative dashboard (if they are an admin) or the homepage (if they are a customer).
 * Invalid login attempts will notify the user of the error.
 */

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel loginPanel;
    private JButton registerButton;
    private UserController userController;
    private CartController cartController;



    public LoginFrame() {
        super("Login");
        setSize(400, 200);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        this.cartController = new CartController();
        this.userController = new UserController(cartController);
        initAll();
        layoutAll();
        activateApp();
    }

    private void initAll() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        loginPanel = new JPanel();

    }

    private void layoutAll() {
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10);

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(usernameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        loginPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(passwordLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        loginPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        loginPanel.add(loginButton, constraints);

        JLabel registerLabel = new JLabel("Not a member?");
        constraints.gridx = 0;
        constraints.gridy = 3;
        loginPanel.add(registerLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        loginPanel.add(registerButton, constraints);

        add(loginPanel, BorderLayout.CENTER);

        pack();

    }

    private void activateApp(){
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredUsername = usernameField.getText();
                String enteredPassword = new String(passwordField.getPassword());

                Optional<String> roleOptional = userController.login(enteredUsername, enteredPassword);

                if (roleOptional.isPresent()) {
                    String role = roleOptional.get();
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login successful!");
                    dispose();
                    if ("admin".equals(role)) {
                        new AdminFrame();
                    } else if ("customer".equals(role)) {
                        new HomepageFrame();
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.");
                    passwordField.setText("");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame();
            }
        });
    }



}
