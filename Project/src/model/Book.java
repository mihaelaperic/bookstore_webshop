package model;

/**
 * Represents a book entity with details such as title,
 * author, price, and quantity.
 * This class captures the essential attributes and behaviors
 * associated with a book in the bookstore application.
 */

public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private Language language;
    private Category category;

    public Book(int id, String title, String author, double price, int quantity, Language language, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.language = language;
        this.category = category;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    @Override
    public String toString() {
        return this.getTitle() + " - $" + this.getPrice();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
