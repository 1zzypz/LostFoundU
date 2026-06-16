package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lostfound.db.DBConnection;

public class UserDA {

    public String[] getUserSessionByEmail(String email) {
        String query = "SELECT id, name, email FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("email")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user session details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        // Try-with-resources auto-closes connection and statement assets automatically
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Queries the database to insert the user data into the database.
    public boolean registerUser(String name, String email, String phone, String password) {
        String query = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, password);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error executing user registration SQL statement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int loginUser(String email, String password) {
        String query = "SELECT password FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");

                    if (dbPassword.equals(password)) {
                        return 1; //success
                    } else {
                        return -1; //Wrong password
                    }
                } else {
                    return 0; //user does not exist
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing login validation statement: " + e.getMessage());
            e.printStackTrace();
            return -2;
        }
    }

    public boolean forgotPassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error executing password update SQL statement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
