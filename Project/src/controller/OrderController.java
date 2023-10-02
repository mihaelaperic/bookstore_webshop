package controller;

import database.OrderDAO;
import model.Book;
import model.Order;
import model.OrderItem;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * Responsible for managing customer orders.
 * This involves creating new orders, updating existing orders, fetching order details,
 * and ensuring the integrity of the order process.
 * Communicates with the OrderDAO class
 */

public class OrderController {

    private OrderDAO orderDAO;

    public OrderController() {
        this.orderDAO = new OrderDAO();
    }

    public boolean createOrder(User user, Map<Book, Integer> cartItems) {
        Order order = new Order(0, user, new ArrayList<>());

        for (Map.Entry<Book, Integer> cartItem : cartItems.entrySet()) {
            Book book = cartItem.getKey();
            int quantity = cartItem.getValue();
            double priceAtOrder = book.getPrice();
            OrderItem orderItem = new OrderItem(0, book, quantity, priceAtOrder);
            order.addOrderItem(orderItem);

        }
        System.out.println("Number of order items: " + order.getOrderItems().size());

        if(orderDAO.processOrder(order)){
            return orderDAO.saveOrder(order);
        }
        return false;
    }

    /**
     * Fetches the last ten orders from the database.
     *
     * @return A list of the last ten orders.
     */
    public List<Order> getLastTenOrders() {
        return orderDAO.getLastTenOrders();
    }

    public List<Order> getOrderByUserId(int userId){
     return orderDAO.getOrdersByUserId(userId);
    }

}
