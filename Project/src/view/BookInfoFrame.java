package view;

import controller.CartController;
import model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A graphical user interface frame that displays detailed information about a selected book.
 * <p>
 * The frame provides a structured view of a book's details, including its title, author, and price.
 * Users have the option to add the book to their cart and explore further information about the book using
 * a "Read more" button, which directs them to a Google search of the book's title.
 * </p>
 * It utilizes the {@link CartController} to manage cart-related actions and the {@link Book} model to display
 * the book's attributes.
 *
 * @see controller.CartController
 * @see model.Book
 * @see javax.swing.JFrame
 * @see java.awt.Desktop
 */

public class BookInfoFrame extends JFrame {
    private JPanel bookInfoPanel;
    private JPanel buttonPanel;
    private JButton addToCartButton;
    private JButton readMoreButton;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel priceLabel;
    private CartController cartController;
    private Book book;


    public BookInfoFrame(Book book, CartController cartController){
        super("About");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        this.cartController = cartController;
        this.book = book;

        initAll(book);
        layoutAll();
        activateApp();

        setVisible(true);
    }

    private void initAll(Book book) {

        bookInfoPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel();
        addToCartButton = new JButton("Add to Cart");
        readMoreButton = new JButton("Read more");

        titleLabel = new JLabel("Title: " + book.getTitle());
        authorLabel = new JLabel("Author: " + book.getAuthor());
        priceLabel = new JLabel("Price: $" + book.getPrice());
    }

    private void layoutAll() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        bookInfoPanel.add(titleLabel, gbc);
        bookInfoPanel.add(authorLabel, gbc);
        bookInfoPanel.add(priceLabel, gbc);

        bookInfoPanel.setBorder(BorderFactory.createTitledBorder("About"));

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addToCartButton);
        buttonPanel.add(readMoreButton);

        add(bookInfoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void activateApp() {
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cartController.addBookToCart(book, 1);

                JOptionPane.showMessageDialog(BookInfoFrame.this, "Book added to cart!");

                BookInfoFrame.this.dispose();
            }
        });

        readMoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookTitle = book.getTitle();
                String encodedTitle = URLEncoder.encode(bookTitle, StandardCharsets.UTF_8);
                String googleSearchURL = "https://www.google.com/search?q=" + encodedTitle;

                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(googleSearchURL));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(BookInfoFrame.this, "Error at connecting...");
                }
            }
        });
    }
}
