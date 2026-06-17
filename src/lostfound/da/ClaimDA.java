package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import lostfound.db.DBConnection;
import lostfound.model.ClaimRequest;

public class ClaimDA {

    //method to retrieve claim record from db
    public DefaultTableModel getAllClaimsTableModel() {
        //table column
        String[] columns = {"Claim ID", "User ID", "Found Item ID", "Proof", "Status", "Reason", "Claim Date"};
        //create table tht is not editable 
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            //to prevent the user to edit the table
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //query to retrieve claim report
        String sql = "SELECT claim_id, user_id, found_item_id, proof_description, "
                + "claim_status, rejection_reason, claim_date FROM claims "
                + "ORDER BY claim_date DESC, claim_id DESC";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("claim_id"),
                    rs.getString("user_id"),
                    rs.getString("found_item_id"),
                    rs.getString("proof_description"),
                    rs.getString("claim_status"),
                    rs.getString("rejection_reason"),
                    rs.getDate("claim_date")
                });
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error loading all claims: " + e.getMessage());
            e.printStackTrace(); //print error message
        }

        return model;
    }

    //method to retrieve all claim report by specific user
    public DefaultTableModel getClaimsByUserTableModel(String userID) {
        //column for the table in gui
        String[] columns = {"Claim ID", "Found Item ID", "Proof", "Status", "Reason", "Claim Date"};
        //create table that is not edittable 
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            //is to prevent the user to edit the table
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //query to retrieve the claim submitted by the user
        String sql = "SELECT claim_id, found_item_id, proof_description, claim_status, "
                + "rejection_reason, claim_date FROM claims WHERE user_id = ? "
                + "ORDER BY claim_date DESC, claim_id DESC";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID);

            //execute query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("claim_id"),
                        rs.getString("found_item_id"),
                        rs.getString("proof_description"),
                        rs.getString("claim_status"),
                        rs.getString("rejection_reason"),
                        rs.getDate("claim_date")
                    });
                }
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error loading user claims: " + e.getMessage());
            e.printStackTrace(); //print error message
        }

        return model;
    }

    //method to check whether the item id is belong to found item
    public boolean isClaimableFoundItem(String itemID) {
        //query to select record with specific id and make sure that the item_type is FOUND
        String sql = "SELECT COUNT(*) FROM items WHERE item_id = ? AND item_type = 'FOUND'";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemID);

            //execute query
            try (ResultSet rs = pstmt.executeQuery()) {
                //check if a result is returned
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error checking found item: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        return false;
    }

    //method untuk insert claim dalam db
    public boolean insertClaim(ClaimRequest claim) {
        //query untuk insert claim dalam db
        String sql = "INSERT INTO claims (claim_id, user_id, found_item_id, proof_description, "
                + "claim_status, rejection_reason, claim_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, claim.getClaimID());
            pstmt.setString(2, claim.getUserID());
            pstmt.setString(3, claim.getFoundItemID());
            pstmt.setString(4, claim.getProofDescription());
            pstmt.setString(5, claim.getClaimStatus());
            pstmt.setString(6, claim.getRejectionReason());
            pstmt.setDate(7, java.sql.Date.valueOf(claim.getClaimDate()));

            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display error message
            System.err.println("Error inserting claim request: " + e.getMessage());
            e.printStackTrace(); //print error message
            return false;
        }
    }

    //method to approve the claim
    public boolean approveClaim(String claimID, String foundItemID) {
        //query to update the claim status to approved
        String updateClaimSql = "UPDATE claims SET claim_status = 'APPROVED', rejection_reason = NULL WHERE claim_id = ?";
        //query to update the item status to claimed
        String updateItemSql = "UPDATE items SET status = 'CLAIMED' WHERE item_id = ?";

        //create db connection
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            //create prepare statmetn for both update query which is for claim and item table
            try (PreparedStatement claimStmt = conn.prepareStatement(updateClaimSql); PreparedStatement itemStmt = conn.prepareStatement(updateItemSql)) {

                claimStmt.setString(1, claimID);
                int claimRows = claimStmt.executeUpdate(); //execute claim status update

                itemStmt.setString(1, foundItemID);
                int itemRows = itemStmt.executeUpdate(); //execute item status update

                //commit update to database
                if (claimRows > 0 && itemRows > 0) {
                    conn.commit();
                    return true;
                }

                conn.rollback(); //rollback kalau upadte fail
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            //display error message
            System.err.println("Error approving claim: " + e.getMessage());
            e.printStackTrace(); //print error message
        }
        return false;
    }

    //method for staff to reject the claim
    public boolean rejectClaim(String claimID, String reason) {
        //query to update the claim status to rejected
        String sql = "UPDATE claims SET claim_status = 'REJECTED', rejection_reason = ? WHERE claim_id = ?";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reason);
            pstmt.setString(2, claimID);

            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display error message
            System.err.println("Error rejecting claim: " + e.getMessage());
            e.printStackTrace(); //print error meessage
            return false;
        }
    }

    //method to updte the user claim
    public boolean updateUserClaimProof(String claimID, String userID, String proofDescription) {
        //query to update the claim
        String sql = "UPDATE claims SET proof_description = ? WHERE claim_id = ? AND user_id = ? AND claim_status = 'PENDING'";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proofDescription);
            pstmt.setString(2, claimID);
            pstmt.setString(3, userID);

            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display error message
            System.err.println("Error updating claim proof: " + e.getMessage());
            e.printStackTrace(); //print error message
            return false;
        }
    }

    //mtehod to delete the user claim
    public boolean deleteUserClaim(String claimID, String userID) {
        String sql = "DELETE FROM claims WHERE claim_id = ? AND user_id = ? AND claim_status = 'PENDING'";

        //create db connection
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, claimID);
            pstmt.setString(2, userID);

            //execute query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            //display error message
            System.err.println("Error deleting user claim: " + e.getMessage());
            e.printStackTrace(); //display error message
            return false;
        }
    }
}
