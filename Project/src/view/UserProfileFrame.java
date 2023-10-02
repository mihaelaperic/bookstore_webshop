package view;

import controller.CartController;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The `UserProfileFrame` class presents a user-centric graphical interface within the bookstore application, facilitating users to view their profile-related functionalities.
 * <p>
 * On accessing the profile, users are greeted with a welcome message featuring their username. Two main options are available for the users: viewing all their past orders and inspecting their current shopping cart.
 * <p>
 * Clicking the respective buttons opens the relevant frames, either the "All orders" frame or the "Cart" frame. This class provides a cohesive and intuitive way for users to manage their shopping activities and review their order history.
 */

public class UserProfileFrame extends JFrame {

    private JPanel profilePanel;
    private JButton myCartButton;
    private JButton myOrdersButton;
    private CartController cartController;

    public UserProfileFrame(CartController cartController) {
        super("My profile");
        setSize(400, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        this.cartController = cartController;
        initAll();
        layoutAll();
        activateApp();
    }

    private void initAll() {
        profilePanel = new JPanel();
        myOrdersButton = new JButton("All orders");
        myCartButton = new JButton("Cart");

    }

    private void layoutAll() {
        profilePanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10);

        JLabel welcomeLabel = new JLabel("Welcome " + SessionManager.getInstance().getCurrentUser().getUsername());
        constraints.gridx = 0;
        constraints.gridy = 0;
        profilePanel.add(welcomeLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        profilePanel.add(myOrdersButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        profilePanel.add(myCartButton, constraints);

        add(profilePanel, BorderLayout.CENTER);


    }

    private void activateApp() {

        myOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrdersFrame();
            }
        });

        myCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new CartFrame(cartController);
            }
        });


    }


}
