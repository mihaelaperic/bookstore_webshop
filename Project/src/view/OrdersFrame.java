package view;

import controller.OrderController;
import model.Order;
import model.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/***
 The `OrdersFrame` class provides a graphical user interface (GUI) for viewing order details within the bookstore application.
 * <p>
 * Displayed in a table format, the frame shows the order ID, user ID, and order date. The content of the table varies based on the user's role.
 * <p>
 * Administrators see the last ten orders from all users, while customers see only their own order history. This distinction is managed using the `SessionManager` to determine the current user's role.
 */

public class OrdersFrame extends JFrame{
    private JPanel ordersPanel;
    private JPanel buttonPanel;
    private JButton refresh;
    private JTable ordersTable;
    private OrderController orderController = new OrderController();

    public OrdersFrame() {

        super("Orders");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        initAll();
        layoutAll();
        activateApp();

    }

    private void initAll() {
        buttonPanel = new JPanel();
        ordersPanel = new JPanel();
        refresh = new JButton("Refresh");
        ordersTable = new JTable();
        ordersTable.setEnabled(false);
        populateOrdersTable();
    }

    private void layoutAll() {
        ordersPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(refresh);
        add(ordersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void activateApp(){
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateOrdersTable();
            }
        });
    }

    private void populateOrdersTable() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            DefaultTableModel model;

            @Override
            protected Void doInBackground() {

                if (SessionManager.getInstance().getCurrentUser().getRole().equals("admin")) {
                    String[] columnNames = {"User", "Order Id", "Order Date"};
                    model = new DefaultTableModel(columnNames, 0);
                    List<Order> orders = orderController.getLastTenOrders();
                    for (Order order : orders) {
                        Object[] row = new Object[]{order.getUserID(), order.getId(), order.getOrderDate()};
                        model.addRow(row);
                    }
                } else if (SessionManager.getInstance().getCurrentUser().getRole().equals("customer")) {
                    String[] columnNames = {"Order Date", "Number of Items Bought", "Total Price"};
                    model = new DefaultTableModel(columnNames, 0);
                    List<Order> orders = orderController.getOrderByUserId(SessionManager.getInstance().getCurrentUser().getId());
                    for (Order order : orders) {
                        Object[] row = new Object[]{order.getOrderDate(), order.getTotalQuantity(), order.getTotalPrice()};
                        model.addRow(row);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                ordersTable.setModel(model);
            }
        };

        worker.execute();
    }


}
