package database;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static database.UserDAO.getUserById;

/**
 * Represents a data access object (DAO) for {@link Order} entities in the bookstore application.
 * <p>
 * Primary responsibilities of this DAO include:
 * <ul>
 *     <li>Persisting new orders and associated items to the database.</li>
 *     <li>Processing orders by verifying book availability and updating the inventory.</li>
 *     <li>Retrieving specific orders, such as the most recent orders or those linked to a certain user.</li>
 *     <li>Fetching items related to a particular order.</li>
 * </ul>
 * </p>
 * This class leverages JDBC for database interactions, ensuring proper connection and transaction management.
 *
 * @see Order
 * @see OrderItem
 * @see DatabaseConnectionManager
 */

public class OrderDAO {

    private Connection connection;

    public OrderDAO(){
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    /**
     * Saves a provided order to the database. This method persists both the order
     * information and its associated items into their respective tables.
     *
     * @param order The order to be saved.
     * @return true if the order and its items are saved successfully, false otherwise.
     */
    public boolean saveOrder(Order order) {
        String insertOrderSQL = "INSERT INTO orders (user_id, order_date) VALUES (?, ?)";
        String insertOrderItemSQL = "INSERT INTO order_items (order_id, book_id, quantity, price_at_order) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getUser().getId());
                orderStmt.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));

                int affectedRows = orderStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to save order, no rows affected.");
                }

                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);

                        try (PreparedStatement itemsStmt = conn.prepareStatement(insertOrderItemSQL)) {
                            for (OrderItem item : order.getOrderItems()) {
                                itemsStmt.setInt(1, orderId);
                                itemsStmt.setInt(2, item.getBook().getId());
                                itemsStmt.setInt(3, item.getQuantity());
                                itemsStmt.setDouble(4, item.getPriceAtOrder());
                                itemsStmt.addBatch();
                            }
                            System.out.println("Order items saved for Order ID: " + orderId);
                            int[] updateCounts = itemsStmt.executeBatch();
                            System.out.println("Batch updates: " + Arrays.toString(updateCounts));
                        } catch (BatchUpdateException bue) {
                        System.out.println("Error with batch update: " + bue.getMessage());
                    }

                } else {
                        throw new SQLException("Failed to retrieve order ID.");
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


    /**
     * Processes and finalizes the given order. This involves ensuring that the books
     * in the order are in stock, and then decrementing the quantity of each book in the
     * database to reflect the order.
     *
     * @param order The order to be processed.
     * @return true if the order is processed successfully (i.e., all books are in stock
     *         and their quantities have been updated), false otherwise.
     */
    public boolean processOrder(Order order) {
        Connection conn = null;
        try {
            conn = DatabaseConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            for (OrderItem item : order.getOrderItems()) {
                int bookId = item.getBook().getId();
                int orderedQuantity = item.getQuantity();
                if (!BookDAO.isBookInStock(bookId, orderedQuantity)) {
                    System.out.println("Book with ID " + bookId + " is out of stock or doesn't have enough quantity.");
                    conn.rollback();
                    return false;
                }

                if (!BookDAO.decrementBookQuantity(conn, bookId, orderedQuantity)) {
                    System.out.println("Failed to update quantity for book with ID " + bookId);
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException innerE) {
                innerE.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches the last ten orders stored in the database.
     *
     * @return A list containing the most recent ten orders.
     */
    public List<Order> getLastTenOrders() {
        List<Order> orders = new ArrayList<>();

        String query = "SELECT * FROM orders ORDER BY order_date DESC LIMIT 10;";

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                Date orderdate = rs.getDate("order_date");

                User user = getUserById(userId);

                List<OrderItem> orderItems = getOrderItemsByOrderId(orderId);

                double totalPrice = orderItems.stream().mapToDouble(item -> item.getPriceAtOrder() * item.getQuantity()).sum();
                int totalQuantity = orderItems.stream().mapToInt(OrderItem::getQuantity).sum();

                Order order = new Order(orderId, user, orderItems);
                order.setTotalPrice(totalPrice);
                order.setTotalQuantity(totalQuantity);
                order.setOrderDate(orderdate);
                orders.add(order);
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all orders associated with a specific user, identified by their user ID.
     *
     * @param userId The unique identifier of the user for whom orders are to be fetched.
     * @return A list containing all orders associated with the given user.
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();

        String query = "SELECT o.id AS order_id, o.order_date, " +
                "SUM(od.quantity) AS total_quantity, " +
                "SUM(od.price_at_order * od.quantity) AS total_price " +
                "FROM orders o " +
                "JOIN order_items od ON o.id = od.order_id " +
                "WHERE o.user_id = ? " +
                "GROUP BY o.id, o.order_date";

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    Date orderDate = rs.getDate("order_date");
                    int totalQuantity = rs.getInt("total_quantity");
                    double totalPrice = rs.getDouble("total_price");

                    User currentUser = getUserById(userId);
                    List<OrderItem> currentOrderItems = getOrderItemsByOrderId(orderId);

                    Order order = new Order(orderId, currentUser, currentOrderItems);
                    order.setOrderDate(orderDate);
                    order.setTotalQuantity(totalQuantity);
                    order.setTotalPrice(totalPrice);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

    /**
     * Fetches all order items associated with a specific order, identified by its order ID.
     *
     * @param orderId The unique identifier of the order for which items are to be fetched.
     * @return A list containing all items associated with the given order.
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();

        String query = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderItemId = rs.getInt("id");
                    int bookId = rs.getInt("book_id");
                    Book book = BookDAO.getBookById(bookId);
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price_at_order");

                    OrderItem orderItem = new OrderItem(orderItemId, book, quantity, price);
                    orderItems.add(orderItem);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orderItems;
    }
}
