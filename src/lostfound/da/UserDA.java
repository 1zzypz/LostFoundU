package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lostfound.db.DBConnection;

public class UserDA {

    // method to get the user session using email
    public String[] getUserSessionByEmail(String email) {
        String query = "SELECT id, name, email FROM users WHERE email = ?"; // to fetch the id, name, and email based on the user email

        // create database connection with the DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email); //set the email parameter

            //execute the query
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
            //error message when the session is failed
            System.err.println("Error loading user session details: " + e.getMessage());
            e.printStackTrace(); //print error
        }
        //return null if not user was found
        return null;
    }

    //method to check whether the email is already being used by other user
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?"; //query to check if the email is alrdy exists.

        // Try-with-resources auto-closes connection and statement assets automatically
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            
            pstmt.setString(1, email); // set the email parameter
            
            //execute query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); //print the errror
        }
        return false; //return false if the email does not exists, means user boleh register guna the entered email
    }

    //method to insert the new registered user into the datbase.
    public boolean registerUser(String name, String email, String phone, String password) {
        String query = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)"; //insert into the column and their values

        // create database connection with the DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name); //set name parameter
            pstmt.setString(2, email); //set email parameter
            pstmt.setString(3, phone); // set phone parameter
            pstmt.setString(4, password); //set password parameter

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            //display registration error message
            System.err.println("Error executing user registration SQL statement: " + e.getMessage());
            e.printStackTrace(); //print the error
            return false; //return false if the registration fail
        }
    }

    //method to validate the user credentials untuk user login
    public int loginUser(String email, String password) {
        String query = "SELECT password FROM users WHERE email = ?"; //query to retrieve the password from the email entered.

        //create database connection with the DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email); // set the email parameter

            //execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password"); //retrieve the password from db

                    // compare the entered password with db
                    if (dbPassword.equals(password)) {
                        return 1; //login success
                    } else {
                        return -1; //Wrong password
                    }
                } else {
                    return 0; //user does not exist
                }
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error executing login validation statement: " + e.getMessage());
            e.printStackTrace(); //print error message
            return -2; 
        }
    }

    // method to update the user current password with a new one
    public boolean forgotPassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?"; 

        //create database connection with DBConnection class
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newPassword); //set new password parameter
            pstmt.setString(2, email); //set email parameter

            //execute update password
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            //display error message
            System.err.println("Error executing password update SQL statement: " + e.getMessage());
            e.printStackTrace();//print error messge
            return false; //return fales if the update is fail
        }
    }
}
