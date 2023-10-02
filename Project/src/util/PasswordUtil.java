package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for handling password operations, utilizing the BCrypt hashing algorithm.
 * <p>
 * This class provides two primary functions:
 * 1. Hashing a plain-text password.
 * 2. Checking if a plain-text password matches a previously hashed version.
 * <p>
 * By using BCrypt, the passwords are securely hashed with a salt to prevent rainbow table
 * attacks and ensure security in storage and verification. Clients of this class should
 * use it to securely manage user passwords and ensure they are never stored in their plain-text form.
 */

public class PasswordUtil {

    /**
     * Hashes a given plain-text password using BCrypt.
     *
     * @param plainTextPassword The plain-text password to be hashed.
     * @return The hashed version of the provided password.
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if the provided plain-text password matches the hashed version.
     *
     * @param plainTextPassword The plain-text password to be checked.
     * @param hashedPassword The hashed password to which the plain-text password is to be compared.
     * @return true if the plain-text password matches the hashed version, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
