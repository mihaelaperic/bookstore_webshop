package controller;

import database.UserDAO;
import model.SessionManager;
import model.User;
import util.PasswordUtil;

import java.util.Optional;

/***
 * Manages user-related activities such as authentication, user profile updates,
 * password changes, and other user account-related operations.
 * Communicates with the UserDAO class
 */

public class UserController {

    private UserDAO userDAO;
    private CartController cartController;

    public UserController(CartController cartController) {
        this.cartController = cartController;
        this.userDAO = new UserDAO();
    }

    public Optional<User> verifyLogin(String username, String password) {
        Optional<User> optionalUser = userDAO.getUserByUsername(username);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                return optionalUser;
            }
        }
        return Optional.empty();
    }

    public Optional<String> login(String username, String password) {
        Optional<User> user = verifyLogin(username, password);
        if (user.isPresent()) {
            SessionManager.getInstance().login(user.get());
            return Optional.of(user.get().getRole());
        } else {
            return Optional.empty();
        }
    }

    public void logout(){
        SessionManager.getInstance().logout();
    }


    public boolean registerUser(String username, String email, String plainPassword, String role) {
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User user = new User(-1, username, email, hashedPassword, role);
        return userDAO.addUser(user);
    }
}
