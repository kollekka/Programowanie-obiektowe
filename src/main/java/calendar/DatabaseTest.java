package calendar;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {

    public static boolean testConnection(String url, String username, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
            if (conn != null) {
                System.out.println("Connected to the database!");
                return true;
            } else {
                System.out.println("Failed to connect to the database!");
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred:");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(conn != null)
                    conn.close();
            } catch (Exception ex) {
                System.out.println("An error occurred during closing the connection:");
                ex.printStackTrace();
            }
        }
    }
}