package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the user's session during their interaction with the application.
 * Keeps track of the logged-in user and provides session-specific
 * functionalities, such as order details and cart state.
 */

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private List<OrderItem> currentOrderItems = new ArrayList<>();

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<OrderItem> getCurrentOrderItems() {
        return currentOrderItems;
    }

    public void addToOrder(OrderItem item) {
        this.currentOrderItems.add(item);
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public void clearCurrentOrderItems() {
        if (this.currentOrderItems != null) {
            this.currentOrderItems.clear();
        }
    }

    public boolean isOrderValid() {
        for (OrderItem item : currentOrderItems) {
            Book book = item.getBook();
            if (book.getQuantity() <= 0) {
                return false;
            }
        }
        return true;
    }



}
