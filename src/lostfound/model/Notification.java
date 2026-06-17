package lostfound.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Notification {

    public static final String TYPE_CLAIM_APPROVED = "CLAIM_APPROVED";
    public static final String TYPE_CLAIM_REJECTED = "CLAIM_REJECTED";
    public static final String TYPE_ITEM_VERIFIED = "ITEM_VERIFIED";

    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private String notifID;
    private String recipientID;
    private String message;
    private String type;
    private LocalDateTime timestamp;
    private boolean readStatus;

    public Notification(String recipientID, String message, String type) {
        this.notifID = "NTF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.recipientID = recipientID;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.readStatus = false;
    }

    public String getNotifID() {
        return notifID;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return readStatus;
    }

    public void markAsRead() {
        readStatus = true;
    }

    public String getNotifDetails() {
        String marker = readStatus ? "" : "* ";
        return "[" + timestamp.format(FORMATTER) + "] " + marker + message;
    }

    public static Notification claimApproved(String recipientID, String claimID, String itemName) {
        return new Notification(recipientID,
                "Your claim " + claimID + " for " + itemName + " was approved.",
                TYPE_CLAIM_APPROVED);
    }

    public static Notification claimRejected(String recipientID, String claimID, String reason) {
        return new Notification(recipientID,
                "Your claim " + claimID + " was rejected. Reason: " + reason,
                TYPE_CLAIM_REJECTED);
    }

    public static Notification itemVerified(String recipientID, String itemName) {
        return new Notification(recipientID,
                "Your found item was verified: " + itemName,
                TYPE_ITEM_VERIFIED);
    }
}
