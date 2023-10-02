package controller;

import database.BookDAO;
import model.Book;
import model.Category;

import java.util.List;

/***
 * Manages the retrieval, update, and manipulation of book-related data.
 * This includes operations such as fetching all books, searching books
 * by specific criteria, and handling book-specific tasks.
 * Communicates with the BookDAO class
 */

public class BookController {

    private BookDAO bookDAO;

    public BookController() {
        this.bookDAO = new BookDAO();
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public Book getBookById(int id) {
        return BookDAO.getBookById(id);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookDAO.searchBooksByTitle(title);
    }

    /**
     * Fetches books of a given category.
     *
     * @param category The category of books to retrieve.
     * @return A list of books of the given category.
     */
    public List<Book> getBooksByCategory(Category category) {
        return bookDAO.getBooksByCategory(category);
    }

}
