package view;

import controller.CartController;
import controller.OrderController;
import model.Book;
import model.SessionManager;
import model.User;
import view.tablemodels.CartTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;


/**
 * The CartFrame provides a user interface for reviewing and managing items in the user's shopping cart.
 * <p>
 * Features include:
 * - Displaying a list of books added to the cart.
 * - Showing the total price for all items.
 * - Options to proceed with purchasing items or emptying the cart.
 * <p>
 * The frame interfaces with the CartController to reflect cart changes in real-time.
 */

public class CartFrame extends JFrame {
    private JTable cartTable;
    private JLabel totalLabel;
    private JButton buyButton;
    private JButton emptyCartButton;
    private JButton removeButton;
    private JPanel cartPanel;
    private JPanel bottomPanel;
    private CartController cartController;
    private OrderController orderController;

    public CartFrame(CartController cartController) {
        super("Shopping Cart");

        setSize(400, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        this.cartController = cartController;
        this.orderController = new OrderController();

        initAll();
        layoutAll();
        activateApp();

        updateCartView();
    }

    private void initAll() {
        cartTable = new JTable(new CartTableModel(cartController.getCart().getItems()));
        cartTable.setFillsViewportHeight(true);

        totalLabel = new JLabel("Total: $0");
        bottomPanel = new JPanel();
        cartPanel = new JPanel();
        buyButton = new JButton("Buy");
        emptyCartButton = new JButton("Empty Cart");
        removeButton = new JButton("Remove");

        cartTable.getColumnModel().getColumn(0).setCellRenderer(cartTable.getDefaultRenderer(Boolean.class));

        cartTable.getColumnModel().getColumn(0).setCellEditor(cartTable.getDefaultEditor(Boolean.class));

        if(cartController.getCart().getItems().isEmpty()){
            buyButton.setEnabled(false);
            emptyCartButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }

    private void layoutAll() {
        cartPanel.setLayout(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(totalLabel, BorderLayout.NORTH);

        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(buyButton);
        bottomPanel.add(removeButton);
        bottomPanel.add(emptyCartButton);


        add(bottomPanel, BorderLayout.SOUTH);
        add(cartPanel, BorderLayout.CENTER);


    }

    private void activateApp() {
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                Map<Book, Integer> cartItems = cartController.getCart().getItems();

                setAllComponentsEnabled(false);

                PaymentFrame paymentFrame = new PaymentFrame();

                paymentFrame.addPaymentEventListener(event -> {
                    if (event.isPaymentSuccessful()) {
                        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                            @Override
                            protected Boolean doInBackground() throws Exception {
                                return orderController.createOrder(currentUser, cartItems);
                            }

                            @Override
                            protected void done() {
                                try {
                                    boolean orderSuccessful = get();
                                    if (orderSuccessful) {
                                        cartController.getCart().clearCart();
                                        updateCartView();

                                    } else {
                                        JOptionPane.showMessageDialog(CartFrame.this, "There was an issue processing your order. Please check stock availability.");
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(CartFrame.this, "An error occurred while processing your order.");
                                } finally {
                                    updateCartView();
                                    setAllComponentsEnabled(false);
                                }
                            }
                        };
                        updateCartView();
                        worker.execute();
                    }
                });
                updateCartView();
                paymentFrame.setVisible(true);
                setAllComponentsEnabled(false);

            }
        });




        emptyCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAllComponentsEnabled(false);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = cartTable.getSelectedRow();

                if (selectedRowIndex == -1) {
                    JOptionPane.showMessageDialog(CartFrame.this, "Please select a book to remove.");
                    return;
                }

                int modelRowIndex = cartTable.convertRowIndexToModel(selectedRowIndex);
                CartTableModel tableModel = (CartTableModel) cartTable.getModel();
                Book bookToRemove = tableModel.getBookAt(modelRowIndex);

                if (bookToRemove != null) {
                    cartController.getCart().removeBook(bookToRemove);
                    updateCartView();
                }
            }
        });




    }

    private void updateCartView() {
        CartTableModel tableModel = new CartTableModel(cartController.getCart().getItems());
        cartTable.setModel(tableModel);

        double total = 0.0;
        for (Map.Entry<Book, Integer> entry : cartController.getCart().getItems().entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
            System.out.println("It's currently: " + total);
        }
        totalLabel.setText("Total: $" + total);
    }

    private void setAllComponentsEnabled(boolean enabled) {
        buyButton.setEnabled(enabled);
        emptyCartButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        cartTable.setEnabled(enabled);
    }

}
