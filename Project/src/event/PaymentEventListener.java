package event;

/**
 * The `PaymentEventListener` is an interface within the bookstore application that defines the contract for components interested in being notified about payment events.
 * <p>
 * Implementing classes are required to provide an implementation for the `paymentFinalized` method, which is triggered when a payment process concludes. This method takes a `PaymentDataEvent` object as its parameter, providing details on the outcome of the payment operation.
 */
public interface PaymentEventListener {
    void paymentFinalized(PaymentDataEvent event);
}

