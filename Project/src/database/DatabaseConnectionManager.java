package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Manages database connection configurations and provides a Singleton instance for creating
 * JDBC connections to the database.
 * <p>
 * This manager reads database configuration (URL, username, password) from a properties file named
 * 'dbconfig.properties' available in the classpath. The Singleton design pattern ensures that only
 * one instance of the database connection manager exists across the application.
 * </p>
 * JDBC driver for MySQL is assumed to be used based on the hard-coded class name for loading the driver.
 *
 * @see java.sql.Connection
 * @see java.sql.DriverManager
 * @see java.util.Properties
 */

public class DatabaseConnectionManager {

    private static DatabaseConnectionManager instance;
    private String url;
    private String user;
    private String password;

    private DatabaseConnectionManager() {
        loadDatabaseConfig();
    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }


    /**
     * Loads database configuration details such as URL, user, and password
     * from a properties file named 'dbconfig.properties'.
     */
    private void loadDatabaseConfig() {
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find dbconfig.properties");
                return;
            }

            prop.load(input);

            url = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            password = prop.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
