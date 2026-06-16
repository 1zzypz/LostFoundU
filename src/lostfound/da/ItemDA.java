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
    
    public DefaultTableModel getAllItemsTableModel() {
        String[] columns = {"Item ID", "Type", "Name", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String sql = "SELECT item_id, item_type, item_name, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items ORDER BY submitted_date DESC, item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
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
        } catch (SQLException e) {
            System.err.println("Error loading item activity feed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
    
    public DefaultTableModel searchItemsTableModel(String keyword) {
        String[] columns = {"Item ID", "Type", "Name", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String sql = "SELECT item_id, item_type, item_name, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items "
                   + "WHERE item_id LIKE ? OR item_type LIKE ? OR item_name LIKE ? "
                   + "OR description LIKE ? OR category LIKE ? OR color LIKE ? "
                   + "OR status LIKE ? OR location_lost LIKE ? OR location_found LIKE ? "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
        String searchKeyword = "%" + keyword + "%";
        
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
            System.err.println("Error searching item records: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
    
    public DefaultTableModel getFoundItemsTableModel() {
        String[] columns = {"Item ID", "Name", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String sql = "SELECT item_id, item_name, category, color, location_found, date_found, status "
                   + "FROM items WHERE item_type = 'FOUND' "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
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
            System.err.println("Error loading found items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
    
    public DefaultTableModel getUserItemsManagementTableModel(String userID) {
        String[] columns = {"Item ID", "Type", "Name", "Description", "Category", "Color", "Location", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String sql = "SELECT item_id, item_type, item_name, description, category, color, status, "
                   + "COALESCE(location_lost, location_found) AS item_location, "
                   + "COALESCE(date_lost, date_found) AS item_date "
                   + "FROM items WHERE reporter_id = ? "
                   + "ORDER BY submitted_date DESC, item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID);
            
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
            System.err.println("Error loading user item reports: " + e.getMessage());
            e.printStackTrace();
        }
        
        return model;
    }
    
    public boolean insertLostItem(ItemLost lostItem){
        String sql = "INSERT INTO items (item_id, item_type, item_name, description, category, color, status, "
                     + "submitted_date, location_lost, date_lost, reporter_id)"
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            // 1. Core Automated Base Fields (Extracted straight from your Java object logic)
            pstmt.setString(1, lostItem.getItemID());              // Auto-generated by UUID
            pstmt.setString(2, "LOST");                            // Discriminator for Single Table Inheritance
            pstmt.setString(3, lostItem.getItemName());            // User UI Input
            pstmt.setString(4, lostItem.getDescription());         // User UI Input
            pstmt.setString(5, lostItem.getCategory().name());     // Selected Enum converted to String
            pstmt.setString(6, lostItem.getColor());                // User UI Input
            
            // 2. Automated Status & Submission Tracking Fields
            pstmt.setString(7, lostItem.getStatus());              // Auto-set to "PENDING"
            pstmt.setDate(8, java.sql.Date.valueOf(lostItem.getSubmittedDate())); // Auto-set via LocalDate.now()
            
            // 3. Subclass Specific Fields
            pstmt.setString(9, lostItem.getLocationLost());        // User UI Input
            pstmt.setDate(10, java.sql.Date.valueOf(lostItem.getDateLost())); // User UI Input (e.g., from Form)
            pstmt.setString(11, lostItem.getReporterID());          // Programmatically filled from active Session ID
            
            // Execute the insertion query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean insertFoundItem(ItemFound foundItem){
        String sql = "INSERT INTO items (item_id, item_type, item_name, description, category, color, status, "
                     + "submitted_date, location_found, date_found, store_at, reporter_id)"
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                
            pstmt.setString(1, foundItem.getItemID());
            pstmt.setString(2, "FOUND");                           // Discriminator value
            pstmt.setString(3, foundItem.getItemName());            // User UI Input
            pstmt.setString(4, foundItem.getDescription());         // User UI Input
            pstmt.setString(5, foundItem.getCategory().name());     // Enum to String
            pstmt.setString(6, foundItem.getColor());               // User UI Input
            
            // 2. Automated Status & Submission Tracking Fields
            pstmt.setString(7, foundItem.getStatus());              // Auto-sets to STATUS_SUBMITTED
            pstmt.setDate(8, java.sql.Date.valueOf(foundItem.getSubmittedDate())); // Current Date
            
            // 3. Subclass Specific Fields (Found)
            pstmt.setString(9, foundItem.getLocationFound());       // User UI Input
            pstmt.setDate(10, java.sql.Date.valueOf(foundItem.getDateFound())); // User UI Input
            pstmt.setString(11, foundItem.getStoreAt());            // User UI Input
            pstmt.setString(12, foundItem.getSubmitterID());        // Session System Input
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean verifyFoundItem(String itemID) {
        String sql = "UPDATE items SET status = 'VERIFIED' WHERE item_id = ? AND item_type = 'FOUND' AND status = 'SUBMITTED'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error verifying found item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUserItemReport(String itemID, String userID, String itemName,
            String description, String color, String location) {
        String sql = "UPDATE items SET item_name = ?, description = ?, color = ?, "
                   + "location_lost = CASE WHEN item_type = 'LOST' THEN ? ELSE location_lost END, "
                   + "location_found = CASE WHEN item_type = 'FOUND' THEN ? ELSE location_found END "
                   + "WHERE item_id = ? AND reporter_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setString(3, color);
            pstmt.setString(4, location);
            pstmt.setString(5, location);
            pstmt.setString(6, itemID);
            pstmt.setString(7, userID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user item report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteUserItemReport(String itemID, String userID) {
        String sql = "DELETE FROM items WHERE item_id = ? AND reporter_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemID);
            pstmt.setString(2, userID);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user item report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
