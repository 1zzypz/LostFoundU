package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lostfound.db.DBConnection;

public class StaffDA {

    public String[] getStaffSessionByEmail(String email) {
        String query = "SELECT staff_id, name, email FROM staff WHERE email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("staff_id"),
                        rs.getString("name"),
                        rs.getString("email")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int staffLogin(String email, String password) {
        String query = "SELECT password FROM staff WHERE email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getString("password").equals(password)) {
                        return 1; //success
                    }
                    return -1; //wrong password
                }
                return 0; //staff not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
