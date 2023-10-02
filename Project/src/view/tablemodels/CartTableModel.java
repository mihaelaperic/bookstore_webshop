package view.tablemodels;

import database.BookDAO;
import model.Book;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A table model for representing a cart's content, consisting of books and their respective quantities.
 * <p>
 * This model extends the {@link AbstractTableModel} and provides columns for the book title, price, and quantity.
 * Users can edit the quantity column to change the number of copies for a particular book in the cart.
 * </p>
 *
 */
public class CartTableModel extends AbstractTableModel {
    private String[] columnNames = {"Book Title", "Price", "Quantity"};
    private List<Book> books;
    private Map<Book, Integer> quantities;


    /**
     * Constructs a new {@code CartTableModel} with the specified cart items.
     *
     * @param cartItems a map containing books as keys and their respective quantities as values
     */
    public CartTableModel(Map<Book, Integer> cartItems) {
        this.books = new ArrayList<>(cartItems.keySet());
        this.quantities = cartItems;
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return book.getTitle();
            case 1:
                return book.getPrice();
            case 2:
                return quantities.get(book);
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 2;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 2) {
            Book book = books.get(row);

            try {
                int enteredQuantity = Integer.parseInt(String.valueOf(value));
                if (!BookDAO.isBookInStock(book.getId(), enteredQuantity)) {
                    JOptionPane.showMessageDialog(null, "Entered quantity exceeds available stock. Available quantity: " + book.getQuantity());
                    return;
                }

                quantities.put(book, enteredQuantity);
                fireTableCellUpdated(row, col);
            } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(null, "Whoops! Invalid input!");
            }
        }
    }

    public Book getBookAt(int rowIndex) {
        return books.get(rowIndex);
    }

}
