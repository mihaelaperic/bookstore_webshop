package view;

import event.PaymentEventListener;
import event.PaymentDataEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The `PaymentFrame` class provides a graphical user interface (GUI) for processing payments within the bookstore application.
 * <p>
 * Users can choose between two payment methods: PayPal and Stripe. After selecting a method and clicking the "Pay" button, the system attempts to create an order for the current user based on items in their cart.
 * <p>
 * Successful order creation prompts a confirmation message, while failure results in an error notification. Additionally, this class supports the registration of external listeners, notifying them of the payment outcome, whether it's successful or not.
 */
public class PaymentFrame extends JFrame {

    private JPanel paymentPanel;
    private JRadioButton paypal;
    private JRadioButton stripe;
    private JButton pay;
    private List<PaymentEventListener> listeners = new ArrayList<>();

    public PaymentFrame() {
        super("Payment");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);


        initAll();
        layoutAll();
        activateApp();

    }

    private void initAll() {
        paymentPanel = new JPanel();
        paypal = new JRadioButton("PayPal");
        stripe = new JRadioButton("Stripe");
        pay = new JButton("Pay");

    }

    private void layoutAll() {
        paymentPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        paymentPanel.add(paypal, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        paymentPanel.add(stripe, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        paymentPanel.add(pay, constraints);

        add(paymentPanel, BorderLayout.CENTER);

    }

    private void activateApp() {
        pay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Order successfully placed!");
                dispose();
                notifyPaymentListeners(true);
            }
        });
    }

    public void addPaymentEventListener(PaymentEventListener listener) {
        listeners.add(listener);
    }

    private void notifyPaymentListeners(boolean paymentSuccessful) {
        PaymentDataEvent event = new PaymentDataEvent(paymentSuccessful);
        for (PaymentEventListener listener : listeners) {
            listener.paymentFinalized(event);
        }
    }

}