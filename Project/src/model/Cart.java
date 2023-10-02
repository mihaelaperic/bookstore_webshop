package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping cart in the application.
 * Manages a collection of items that a user intends
 * to purchase, providing methods to add, remove, and
 * manipulate cart contents.
 */

public class Cart {
    private Map<Book, Integer> items = new HashMap<>();


    public void addBook(Book book, int quantity) {
        items.put(book, items.getOrDefault(book, 0) + quantity);
    }

    public void removeBook(Book book) {
        if (items.containsKey(book)) {
            int count = items.get(book);
            if (count == 1) {
                items.remove(book);
            } else {
                items.put(book, count - 1);
            }
        }
    }

    public void removeAllOfBook(Book book) {
        items.remove(book);
    }


    public void clearCart() {
        items.clear();

    }

    public Map<Book, Integer> getItems() {
        return items;
    }




}
