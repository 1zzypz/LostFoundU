//aidiel
//aggregation dgn item, claim request untuk generate report
package lostfound.model;

import java.time.LocalDate;
import java.util.UUID;

public class ClaimRequest {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    //encapsulation. keeping data private and allowing access through controlled public methods.
    //other classes cannot directly access or change these variables.
    private String claimID;
    private String userID;
    private String foundItemID;
    private String lostReportID;
    private String proofDescription;
    private String claimStatus;
    private String rejectionReason;
    private LocalDate claimDate;

    public ClaimRequest(String userID, String foundItemID,
            String lostReportID, String proofDescription) {
        this.claimID = "CLM-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
        this.userID = userID;
        this.foundItemID = foundItemID;
        this.lostReportID = lostReportID;
        this.proofDescription = proofDescription;
        this.claimStatus = STATUS_PENDING;
        this.rejectionReason = "";
        this.claimDate = LocalDate.now();
    }

    public String getClaimID() {
        return claimID;
    }

    public String getUserID() {
        return userID;
    }

    public String getFoundItemID() {
        return foundItemID;
    }

    public String getLostReportID() {
        return lostReportID;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setProofDescription(String proofDescription) {
        if (proofDescription == null || proofDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Proof description cannot be empty.");
        }
        this.proofDescription = proofDescription.trim();
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = (rejectionReason != null) ? rejectionReason : "";
    }

    public String getClaimDetails() {
        return "Claim ID: " + claimID + "\n"
                + "User ID: " + userID + "\n"
                + "Found Item: " + foundItemID + "\n"
                + "Lost Report: " + (lostReportID != null ? lostReportID : "N/A") + "\n"
                + "Proof: " + proofDescription + "\n"
                + "Status: " + claimStatus + "\n"
                + "Date: " + claimDate
                + (rejectionReason.isEmpty() ? "" : "\nReason: " + rejectionReason);
    }

    public String getTableSummary() {
        return claimID + " | Item: " + foundItemID
                + " | Status: " + claimStatus
                + " | Date: " + claimDate;
    }
}
