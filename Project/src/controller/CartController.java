package controller;

import model.Book;
import model.Cart;

/**
 * Oversees the user's shopping cart operations.
 * This includes adding/removing items to/from the cart, calculating cart totals,
 * and ensuring the cart is correctly represented throughout the user's session.
 */

public class CartController {

    private Cart cart;

    public CartController() {
        this.cart = new Cart();
    }

    public void addBookToCart(Book book, int quantity) {
        cart.addBook(book, quantity);
    }

    public void removeBookFromCart(Book book) {
        cart.removeBook(book);
    }

    public Cart getCart() {
        return cart;
    }

    public void emptyCart() {
        cart.clearCart();
    }

}
