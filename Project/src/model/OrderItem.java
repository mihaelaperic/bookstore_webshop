package model;

/**
 * Represents a line item in an order.
 * Each OrderItem captures details about a specific book,
 * including its quantity and the price at the time the order was placed.
 */

public class OrderItem {
    private int id;
    private Book book;
    private int quantity;
    private double priceAtOrder;

    public OrderItem(int id, Book book, int quantity, double priceAtOrder) {
        this.id = id;
        this.book = book;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    /**
     * Gets the total price for this specific order item based on the price at the time of the order.
     * @return Total price (priceAtOrder multiplied by quantity)
     */
    public double getTotalPriceForItem() {
        return this.priceAtOrder * this.quantity;
    }
}
