package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a customer's completed transaction.
 * Contains details about the order, such as order date,
 * and associated order items.
 */

public class Order {
    private int id;
    private Date orderDate;
    private List<OrderItem> orderItems;
    private User user;
    private int totalQuantity;
    private double totalPrice;

    public Order(int id, User user, List<OrderItem> orderItems) {
        this.id = id;
        this.orderDate = new Date();
        this.orderItems = orderItems;
        this.user = user;
        this.calculateTotals();
    }

    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = new Date();
    }


    private void calculateTotals() {
        this.totalQuantity = this.orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
        this.totalPrice = this.orderItems.stream().mapToDouble(item -> item.getQuantity() * item.getPriceAtOrder()).sum();
    }

    // getters and setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public int getUserID(){
        return user.getId();
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.calculateTotals();
    }


}
