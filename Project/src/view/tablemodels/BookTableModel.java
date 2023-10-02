package view.tablemodels;

import model.Book;
import model.Category;
import model.Language;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {

    private List<Book> books;
    private List<Boolean> selectedStates = new ArrayList<>();

    private final String[] columnNames = {"Selected", "Title", "Author", "Price", "Language", "Category"};

    public BookTableModel(List<Book> books) {
        this.books = new ArrayList<>(books);
        for (int i = 0; i < books.size(); i++) {
            selectedStates.add(false);
        }
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
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        if (book.getQuantity() <= 0 && columnIndex == 0) {
            return null;
        }
        switch (columnIndex) {
            case 0:
                if (rowIndex >= 0 && rowIndex < selectedStates.size()) {
                    return selectedStates.get(rowIndex);
                } else {
                    return Boolean.FALSE;
                }
            case 1: return book.getTitle();
            case 2: return book.getAuthor();
            case 3: return book.getPrice();
            case 4: return book.getLanguage().getDisplayName();
            case 5: return book.getCategory().toString();
            default: return null;
        }
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Boolean.class;
            case 1, 2: return String.class;
            case 3: return Double.class;
            case 4: return Language.class;
            case 5: return Category.class;
            default: return Object.class;
        }
    }

    public Book getBookAt(int rowIndex) {
        return books.get(rowIndex);
    }

    public void setBooks(List<Book> books) {
        this.books = new ArrayList<>(books);
        this.selectedStates.clear();
        for (Book book : books) {
            this.selectedStates.add(Boolean.FALSE);
        }
        fireTableDataChanged();
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return books.get(rowIndex).getQuantity() > 0 && selectedStates.get(rowIndex) != null;
        }
        return false;
    }



    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && aValue instanceof Boolean) {
            selectedStates.set(rowIndex, (Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }


    public Boolean getSelectedStateAt(int rowIndex) {
        return selectedStates.get(rowIndex);
    }

}
