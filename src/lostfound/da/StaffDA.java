/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lostfound.db.DBConnection;
/**
 *
 * @author danis
 */
public class StaffDA {
    public int staffLogin(String id, String password){
        String query = "SELECT password FROM staff WHERE staff_id = ?";
        
        try(Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setString(1, id);
            
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()) {
                    if(rs.getString("password").equals(password)){
                        return 1; //success
                    }
                    return -1; //wrong password
                }
                return 0; //staff not found
            }
        } catch (SQLException e){
            e.printStackTrace();
            return -2;
        }
    }
}
