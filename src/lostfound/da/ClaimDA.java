package lostfound.da;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import lostfound.db.DBConnection;
import lostfound.model.ClaimRequest;

public class ClaimDA {

    public DefaultTableModel getAllClaimsTableModel() {
        String[] columns = {"Claim ID", "User ID", "Found Item ID", "Proof", "Status", "Reason", "Claim Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT claim_id, user_id, found_item_id, proof_description, "
                + "claim_status, rejection_reason, claim_date FROM claims "
                + "ORDER BY claim_date DESC, claim_id DESC";

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
            System.err.println("Error loading all claims: " + e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    public DefaultTableModel getClaimsByUserTableModel(String userID) {
        String[] columns = {"Claim ID", "Found Item ID", "Proof", "Status", "Reason", "Claim Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT claim_id, found_item_id, proof_description, claim_status, "
                + "rejection_reason, claim_date FROM claims WHERE user_id = ? "
                + "ORDER BY claim_date DESC, claim_id DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userID);

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
            System.err.println("Error loading user claims: " + e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    public boolean isClaimableFoundItem(String itemID) {
        String sql = "SELECT COUNT(*) FROM items WHERE item_id = ? AND item_type = 'FOUND'";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking found item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertClaim(ClaimRequest claim) {
        String sql = "INSERT INTO claims (claim_id, user_id, found_item_id, proof_description, "
                + "claim_status, rejection_reason, claim_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, claim.getClaimID());
            pstmt.setString(2, claim.getUserID());
            pstmt.setString(3, claim.getFoundItemID());
            pstmt.setString(4, claim.getProofDescription());
            pstmt.setString(5, claim.getClaimStatus());
            pstmt.setString(6, claim.getRejectionReason());
            pstmt.setDate(7, java.sql.Date.valueOf(claim.getClaimDate()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting claim request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveClaim(String claimID, String foundItemID) {
        String updateClaimSql = "UPDATE claims SET claim_status = 'APPROVED', rejection_reason = NULL WHERE claim_id = ?";
        String updateItemSql = "UPDATE items SET status = 'CLAIMED' WHERE item_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement claimStmt = conn.prepareStatement(updateClaimSql); PreparedStatement itemStmt = conn.prepareStatement(updateItemSql)) {

                claimStmt.setString(1, claimID);
                int claimRows = claimStmt.executeUpdate();

                itemStmt.setString(1, foundItemID);
                int itemRows = itemStmt.executeUpdate();

                if (claimRows > 0 && itemRows > 0) {
                    conn.commit();
                    return true;
                }

                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error approving claim: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectClaim(String claimID, String reason) {
        String sql = "UPDATE claims SET claim_status = 'REJECTED', rejection_reason = ? WHERE claim_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reason);
            pstmt.setString(2, claimID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting claim: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserClaimProof(String claimID, String userID, String proofDescription) {
        String sql = "UPDATE claims SET proof_description = ? WHERE claim_id = ? AND user_id = ? AND claim_status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proofDescription);
            pstmt.setString(2, claimID);
            pstmt.setString(3, userID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating claim proof: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUserClaim(String claimID, String userID) {
        String sql = "DELETE FROM claims WHERE claim_id = ? AND user_id = ? AND claim_status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, claimID);
            pstmt.setString(2, userID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user claim: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
