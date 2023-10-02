package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The AdminFrame serves as a dashboard for administrative tasks within the online bookstore application.
 * <p>
 * Features include:
 * - A welcoming greeting for the admin.
 * - Navigation buttons to check product availability, view current orders, and access the main webshop.
 * <p>
 * This dashboard provides quick access to essential managerial functionalities in a GridBagLayout.
 */

public class AdminFrame extends JFrame {

    private JLabel welcomeLabel;
    private JButton productAvailabilityButton;
    private JButton currentOrdersButton;
    private JButton webshopButton;
    private JPanel adminPanel;

    public AdminFrame() {
        super("Admin Dashboard");
        setSize(400, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initAll();
        layoutAll();
        activateApp();
    }

    private void initAll() {
        adminPanel = new JPanel();
        productAvailabilityButton = new JButton("Check Product Availability");
        currentOrdersButton = new JButton("View Current Orders");
        webshopButton = new JButton("Go to Webshop");
    }

    private void layoutAll() {
        adminPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10);

        welcomeLabel = new JLabel("Welcome admin!");
        constraints.gridx = 0;
        constraints.gridy = 0;
        adminPanel.add(welcomeLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        adminPanel.add(productAvailabilityButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        adminPanel.add(currentOrdersButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        adminPanel.add(webshopButton, constraints);

        add(adminPanel, BorderLayout.CENTER);

    }

    private void activateApp() {
        productAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BooksFrame();

            }
        });

        currentOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrdersFrame();
            }
        });

        webshopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HomepageFrame();
            }
        });

    }

}
