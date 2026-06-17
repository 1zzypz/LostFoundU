/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import lostfound.db.DBConnection;
import lostfound.model.Item;
import lostfound.model.ItemFound;
import lostfound.model.ItemLost;
/**
 *
 * @author danis
 */
public class ItemDA {
    
    //retrieve all items in the database and display the item in the table
    public DefaultTableModel getAllItemsTableModel() {
        String[] columns = {"Item ID", "Type", "Name", "Category", "Color", "Location", "Date", "Status"}; //table column
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // this is to prevent the user to edit the table
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        //query to fetch the items
        String sql = "SELECT item_id, item_type, item_name, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items ORDER BY submitted_date DESC, item_id DESC";
        
        //create db connection with DBConnection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            //loop through each record returned by the query
            while (rs.next()) {
                
                //untuk add item dekat table di gui
                model.addRow(new Object[] {
                    rs.getString("item_id"),
                    rs.getString("item_type"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getString("color"),
                    rs.getString("item_location"),
                    rs.getDate("item_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error loading item activity feed: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        
        return model; // return table model
    }
    
    //method to search the item based on what user entered
    public DefaultTableModel searchItemsTableModel(String keyword) {
        String[] columns = {"Item ID", "Type", "Name", "Category", "Color", "Location", "Date", "Status"}; 
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        //query to select the item
        String sql = "SELECT item_id, item_type, item_name, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items "
                   + "WHERE item_id LIKE ? OR item_type LIKE ? OR item_name LIKE ? "
                   + "OR description LIKE ? OR category LIKE ? OR color LIKE ? "
                   + "OR status LIKE ? OR location_lost LIKE ? OR location_found LIKE ? "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
        String searchKeyword = "%" + keyword + "%";
        
        //create db connection with CBConnection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 1; i <= 9; i++) {
                pstmt.setString(i, searchKeyword);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("item_id"),
                        rs.getString("item_type"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("color"),
                        rs.getString("item_location"),
                        rs.getDate("item_date"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error searching item records: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        
        return model;
    }
    
    //fetch found item in the database
    public DefaultTableModel getFoundItemsTableModel() {
        String[] columns = {"Item ID", "Name", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        //query untuk select item "Found"
        String sql = "SELECT item_id, item_name, category, color, location_found, date_found, status "
                   + "FROM items WHERE item_type = 'FOUND' "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
        //create db connection with DBConnection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("item_id"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getString("color"),
                    rs.getString("location_found"),
                    rs.getDate("date_found"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error loading found items: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        
        return model;
    }
    
    //to fecth the item report that has been submitted by the specific user
    public DefaultTableModel getUserItemsManagementTableModel(String userID) {
        //column header in the table
        String[] columns = {"Item ID", "Type", "Name", "Description", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // retrieve all item reported by the user
        String sql = "SELECT item_id, item_type, item_name, description, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items WHERE reporter_id = ? "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
        //create db connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID);
            
            //execute query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("item_id"),
                        rs.getString("item_type"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("color"),
                        rs.getString("item_location"),
                        rs.getDate("item_date"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error loading user item reports: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        
        return model;
    }
    
    //method to insert lost item in db
    public boolean insertLostItem(ItemLost lostItem){
        //query to insert the lost item
        String sql = "INSERT INTO items (item_id, item_type, item_name, description, category, color, status, "
                     + "submitted_date, location_lost, date_lost, reporter_id)"
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        //create db connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            

            pstmt.setString(1, lostItem.getItemID());
            pstmt.setString(2, "LOST");
            pstmt.setString(3, lostItem.getItemName());
            pstmt.setString(4, lostItem.getDescription());
            pstmt.setString(5, lostItem.getCategory().name());
            pstmt.setString(6, lostItem.getColor());
            
            pstmt.setString(7, lostItem.getStatus());
            pstmt.setDate(8, java.sql.Date.valueOf(lostItem.getSubmittedDate()));
            
            
            pstmt.setString(9, lostItem.getLocationLost());
            pstmt.setDate(10, java.sql.Date.valueOf(lostItem.getDateLost()));
            pstmt.setString(11, lostItem.getReporterID());
            
            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e){
            e.printStackTrace(); //print error message
            return false;
        }
    }
    
    //method to insert found item in db
    public boolean insertFoundItem(ItemFound foundItem){
        //query to insert found item
        String sql = "INSERT INTO items (item_id, item_type, item_name, description, category, color, status, "
                     + "submitted_date, location_found, date_found, store_at, reporter_id)"
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        //create db connection
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                
            pstmt.setString(1, foundItem.getItemID());
            pstmt.setString(2, "FOUND");
            pstmt.setString(3, foundItem.getItemName());
            pstmt.setString(4, foundItem.getDescription());
            pstmt.setString(5, foundItem.getCategory().name());
            pstmt.setString(6, foundItem.getColor());
            

            pstmt.setString(7, foundItem.getStatus());
            pstmt.setDate(8, java.sql.Date.valueOf(foundItem.getSubmittedDate())); 
            

            pstmt.setString(9, foundItem.getLocationFound());
            pstmt.setDate(10, java.sql.Date.valueOf(foundItem.getDateFound()));
            pstmt.setString(11, foundItem.getStoreAt());
            pstmt.setString(12, foundItem.getSubmitterID());
            
            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace(); //print error message
            return false;
        }
    }
    
    //method for staff to verified item
    public boolean verifyFoundItem(String itemID) {
        String sql = "UPDATE items SET status = 'VERIFIED' WHERE item_id = ? AND item_type = 'FOUND' AND status = 'SUBMITTED'";
        
        //create db connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemID);
            
            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display error message
            System.err.println("Error verifying found item: " + e.getMessage());
            e.printStackTrace(); //print error message
            return false;
        }
    }
    
    //method untuk edit item report
    public boolean updateUserItemReport(String itemID, String userID, String itemName,
            String description, String color, String location) {
        //query untuk edit item
        String sql = "UPDATE items SET item_name = ?, description = ?, color = ?, "
                   + "location_lost = CASE WHEN item_type = 'LOST' THEN ? ELSE location_lost END, "
                   + "location_found = CASE WHEN item_type = 'FOUND' THEN ? ELSE location_found END "
                   + "WHERE item_id = ? AND reporter_id = ?";
        
        //create db connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setString(3, color);
            pstmt.setString(4, location);
            pstmt.setString(5, location);
            pstmt.setString(6, itemID);
            pstmt.setString(7, userID);
            
            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display errror message
            System.err.println("Error updating user item report: " + e.getMessage());
            e.printStackTrace(); //print error message
            return false;
        }
    }
    
    //method untuk delete item report
    public boolean deleteUserItemReport(String itemID, String userID) {
        String sql = "DELETE FROM items WHERE item_id = ? AND reporter_id = ?"; //query untuk select which item to delete dekat db
        
        //create db connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemID); //set itemID parameter
            pstmt.setString(2, userID); //set UserID parameter
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // display error message
            System.err.println("Error deleting user item report: " + e.getMessage());
            e.printStackTrace(); //print error message
            return false;
        }
    }
}
