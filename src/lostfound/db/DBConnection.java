/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lostfound.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author danis
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/lostfoundu"; //database port, and name
    private static final String USER = "root"; //database username
    private static final String PASSWORD = ""; //database password
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load database driver
        } catch (ClassNotFoundException e){
            System.err.println("MySQL JDBC Driver not found!"); //display error message 
            e.printStackTrace(); // print error message
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return (DriverManager.getConnection(URL, USER, PASSWORD)); //create connection with the database
    }
}
