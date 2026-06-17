package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lostfound.db.DBConnection;

public class StaffDA {

    //method to get the staff session
    public String[] getStaffSessionByEmail(String email) {
        String query = "SELECT staff_id, name, email FROM staff WHERE email = ?"; //fetch the staff id, name, and email from the staff email entered

        //create db connection with DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email); //set email parameter

            //execute the query
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
            e.printStackTrace(); //print db error details
        }
        return null; //return null if no staff record is found
    }

    //method to validate staff login credentials
    public int staffLogin(String email, String password) {
        String query = "SELECT password FROM staff WHERE email = ?"; //query to retrieve the password from entered email

        //create database connection with DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email); //set email parameter

            //execute query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getString("password").equals(password)) {
                        return 1; //login success
                    }
                    return -1; //wrong password
                }
                return 0; //staff not found
            }
        } catch (SQLException e) {
            e.printStackTrace(); //print db error details
            return -2; //return database error
        }
    }
}
