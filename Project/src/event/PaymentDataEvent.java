package event;

/**
 * The `PaymentDataEvent` class is a data structure designed to encapsulate and convey the status of a payment action within the bookstore application.
 * <p>
 * It holds a boolean attribute indicating whether the payment was successful. This class serves as a means to notify relevant components or listeners about the outcome of a payment operation.
 */
public class PaymentDataEvent {
    private boolean paymentSuccessful;

    public PaymentDataEvent(boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
}
