package database;

import model.Book;
import model.Category;
import model.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages database connection configurations and provides a Singleton instance for creating
 * JDBC connections to the database.
 * <p>
 * This manager reads database configuration (URL, username, password) from a properties file named
 * 'dbconfig.properties' available in the classpath. The Singleton design pattern ensures that only
 * one instance of the database connection manager exists across the application.
 * </p>
 * JDBC driver for MySQL is assumed to be used based on the hard-coded class name for loading the driver.
 *
 * @see java.sql.Connection
 * @see java.sql.DriverManager
 * @see java.util.Properties
 */

public class BookDAO {
    private Connection connection;

    public BookDAO() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    /**
     * Retrieves a list of all books in the database.
     *
     * @return A list containing all books. If no books are found, returns an empty list.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Language language = Language.valueOf(rs.getString("lang").toUpperCase());
                Category category = Category.fromString(rs.getString("category"));

                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity"), language, category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Fetches a book by its unique identifier from the database.
     *
     * @param id The unique identifier of the desired book.
     * @return A Book object if found, otherwise returns null.
     */
    public static Book getBookById(int id) {
        Book book = null;
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Language language = Language.valueOf(rs.getString("lang").toUpperCase());
                    Category category = Category.valueOf(rs.getString("category"). toUpperCase());
                    book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity"), language, category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    /**
     * Searches for books with a title matching the provided keyword or phrase.
     *
     * @param title The keyword or phrase to search for in book titles.
     * @return A list of books with titles that contain the specified keyword or phrase.
     *         If no matches are found, returns an empty list.
     */
    public List<Book> searchBooksByTitle(String title) {
        List<Book> matchingBooks = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {

            stmt.setString(1, "%" + title + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Language language = Language.valueOf(rs.getString("lang").toUpperCase());
                        Category category = Category.valueOf(rs.getString("category"). toUpperCase());
                        matchingBooks.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity"), language, category));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid language found in the database for book id: " + rs.getInt("id"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchingBooks;
    }

    /**
     * Adds a new book record to the database.
     *
     * @param book The Book object representing the book details to be added.
     * @return true if the book was successfully added, false otherwise.
     */
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getQuantity());
            stmt.setString(5, book.getLanguage().getDisplayName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Decrements the quantity of a specific book in the database, used mainly during purchase operations.
     *
     * @param conn           The active SQL connection.
     * @param bookId         The unique identifier of the book whose quantity needs to be decremented.
     * @param quantityBought The number of books bought (to be decremented from the stock).
     * @return true if the operation was successful, false otherwise.
     * @throws SQLException If any database operation fails.
     */
    public static boolean decrementBookQuantity(Connection conn, int bookId, int quantityBought) throws SQLException {

        if (!isBookInStock(bookId, quantityBought)) {
            return false;
        }
        String query = "UPDATE books SET quantity = quantity - ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantityBought);
            stmt.setInt(2, bookId);
            System.out.println("Decrementing quantity for Book ID: " + bookId + " by " + quantityBought);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected);
            return rowsAffected > 0;
        }
    }


    /**
     * Checks if the desired quantity of a specific book is available in stock.
     *
     * @param bookId         The unique identifier of the desired book.
     * @param desiredQuantity The quantity that a user wants to purchase.
     * @return true if the desired quantity is available in stock, false otherwise.
     */
    public static boolean isBookInStock(int bookId, int desiredQuantity) {
        String query = "SELECT quantity FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int currentStock = rs.getInt("quantity");
                return currentStock >= desiredQuantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a list of all books in a given category from the database.
     *
     * @param category The category of books to retrieve.
     * @return A list containing books of the given category. If no books are found, returns an empty list.
     */
    public List<Book> getBooksByCategory(Category category) {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE category = ?")) {

            stmt.setString(1, category.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Language language = Language.valueOf(rs.getString("lang").toUpperCase());
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getDouble("price"), rs.getInt("quantity"), language, category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }


}
