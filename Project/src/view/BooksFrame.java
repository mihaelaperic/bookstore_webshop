package view;

import controller.BookController;
import model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * The BooksFrame provides a user interface to display and manage the bookstore's book inventory.
 * <p>
 * Features include:
 * - Displaying a table of books with details like ID, Name, Price, and Quantity.
 * - Utilizes the BookController to fetch and display all book details.
 * <p>
 * Note: This frame is mainly for viewing purposes; the table data is not editable.
 */
public class BooksFrame extends JFrame {

    private JPanel books;
    private JTable booksTable;
    private DefaultTableModel model;
    private BookController bookController = new BookController();

    public BooksFrame(){
        super("Books Management");
        setSize(500, 400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initAll();
        layoutAll();
    }

    private void initAll() {
        booksTable = new JTable();
        booksTable.setAutoCreateRowSorter(true);

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable.setModel(model);

        populateBooksTable();
        books = new JPanel();
    }

    private void layoutAll() {
        books.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(booksTable);
        books.add(scrollPane, BorderLayout.CENTER);
        add(books, BorderLayout.CENTER);
    }

    private void populateBooksTable() {
        String[] columnNames = {"ID", "Name", "Price", "Quantity"};
        model.setColumnIdentifiers(columnNames);
        model.setRowCount(0);

        List<Book> books = bookController.getAllBooks();
        for (Book book : books) {
            Object[] row = new Object[]{book.getId(), book.getTitle(), book.getPrice(), book.getQuantity()};
            model.addRow(row);
        }
    }
}
