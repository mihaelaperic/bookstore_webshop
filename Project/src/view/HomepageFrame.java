package view;

import controller.BookController;
import controller.CartController;
import controller.UserController;
import model.*;
import view.tablemodels.BookTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The HomepageFrame serves as the primary user interface for browsing and selecting books
 * within the online bookstore application.
 * <p>
 * Features include:
 * - Displaying a list of in-stock books.
 * - Options to add selected books to the shopping cart.
 * - Navigation buttons to view the cart, user profile, or logout.
 * <p>
 * This frame interacts with various controllers (`BookController`, `CartController`, `UserController`)
 * to manage book data, cart actions, and user sessions. It is designed with a BorderLayout, showcasing
 * books centrally with action buttons positioned at the bottom.
 */

public class HomepageFrame extends JFrame {

    private BookController bookController;
    private CartController cartController;
    private UserController userController;
    private JTable bookTable;
    private BookTableModel tableModel;
    private JButton addToCartButton;
    private JButton goToCartButton;
    private JButton logoutButton;
    private JButton myProfileButton;
    private JPanel homepage;
    private JPanel buttonPanel;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Language> languageComboBox;


    public HomepageFrame() {
        super("Homepage");
        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        this.bookController = new BookController();
        this.cartController = new CartController();
        this.userController = new UserController(cartController);

        initAll();
        layoutAll();
        activateApp();

    }

    private void initAll() {
        // panels
        buttonPanel = new JPanel();
        homepage = new JPanel();
        searchPanel = new JPanel();

        // buttons
        addToCartButton = new JButton("Add to Cart");
        goToCartButton = new JButton("Go to Cart");
        logoutButton = new JButton("Logout");
        myProfileButton = new JButton("Profile");
        searchButton = new JButton("Search");

        // fields
        searchField = new JTextField(20);
        categoryComboBox = new JComboBox<>();
        languageComboBox = new JComboBox<>();

        // combobox
        for (Category category : Category.values()) {
            categoryComboBox.addItem(category);
        }
        for (Language language : Language.values()) {
            languageComboBox.addItem(language);
        }


        // table
        tableModel = new BookTableModel(new ArrayList<>());

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        bookTable.setAutoCreateRowSorter(true);

        bookTable.getColumnModel().getColumn(0).setCellRenderer(bookTable.getDefaultRenderer(Boolean.class));

        bookTable.getColumnModel().getColumn(0).setCellEditor(bookTable.getDefaultEditor(Boolean.class));


        updateBooks(null);

        if(SessionManager.getInstance().getCurrentUser().getRole().equals("admin")){
            addToCartButton.setEnabled(false);
            goToCartButton.setEnabled(false);
        }

    }

    private void layoutAll() {
        homepage.setLayout(new BorderLayout());
        homepage.setBorder(BorderFactory.createTitledBorder("Books"));
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        homepage.add(bookScrollPane, BorderLayout.CENTER);

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addToCartButton);
        buttonPanel.add(goToCartButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(myProfileButton);

        searchPanel.add(categoryComboBox);
        searchPanel.add(languageComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        add(homepage, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void activateApp() {
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int addedCount = 0;
                for (int viewRow = 0; viewRow < bookTable.getRowCount(); viewRow++) {
                    Object cellValue = bookTable.getValueAt(viewRow, 0);
                    if (cellValue == null) {
                        continue;
                    }
                    boolean isSelected;
                    isSelected = (boolean) bookTable.getValueAt(viewRow, 0);
                    if (isSelected) {
                        int modelRow = bookTable.convertRowIndexToModel(viewRow);
                        Book selectedBook = tableModel.getBookAt(modelRow);
                        if (selectedBook != null) {
                            OrderItem orderItem = new OrderItem(0, selectedBook, 1, selectedBook.getPrice());
                            cartController.addBookToCart(orderItem.getBook(),1);
                            addedCount++;
                        }
                    }
                }

                if (addedCount > 0) {
                    updateBooks(null);
                    JOptionPane.showMessageDialog(HomepageFrame.this, "Selected books added to cart.");
                } else {
                    JOptionPane.showMessageDialog(HomepageFrame.this, "No books selected.");
                }
            }
        });


        goToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CartFrame(cartController);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userController.logout();
                dispose();
                new LoginFrame();
            }
        });

        myProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(SessionManager.getInstance().getCurrentUser().getRole().equals("admin")){
                    new AdminFrame();
                }else {
                    new UserProfileFrame(cartController);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                updateBooks(query);
            }
        });



        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedViewRow = bookTable.getSelectedRow();
                    if (selectedViewRow != -1) {
                        int selectedModelRow = bookTable.convertRowIndexToModel(selectedViewRow);
                        Book selectedBook = tableModel.getBookAt(selectedModelRow);
                        if (selectedBook != null) {
                            new BookInfoFrame(selectedBook, cartController);
                        }
                    }
                }
            }
        });


    }

    private void updateBooks(String query) {
        SwingWorker<List<Book>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                List<Book> allBooks = bookController.getAllBooks();
                List<Book> booksToDisplay = new ArrayList<>();

                Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
                Language selectedLanguage = (Language) languageComboBox.getSelectedItem();

                for (Book book : allBooks) {
                    if (book.getQuantity() <= 0) {
                        continue;
                    }

                    if (query != null && !query.isEmpty() && !book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        continue;
                    }

                    if (selectedCategory != Category.VIEW_ALL && book.getCategory() != selectedCategory) {
                        continue;
                    }

                    if (selectedLanguage != Language.VIEW_ALL && book.getLanguage() != selectedLanguage) {
                        continue;
                    }

                    booksToDisplay.add(book);
                }

                return booksToDisplay;
            }

            @Override
            protected void done() {
                try {
                    List<Book> booksToDisplay = get();
                    tableModel.setBooks(booksToDisplay);

                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(HomepageFrame.this, "Don't look, I'm broken!");
                }
            }
        };

        worker.execute();
    }

}
