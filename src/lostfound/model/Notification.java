package lostfound.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Notification {

    public static final String TYPE_CLAIM_APPROVED = "CLAIM_APPROVED";
    public static final String TYPE_CLAIM_REJECTED = "CLAIM_REJECTED";
    public static final String TYPE_ITEM_VERIFIED = "ITEM_VERIFIED";

    //Static formatter shared across all Notification objects to format dates uniformly
    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    //Encapsulation: private fields to hide internal state
    private String notifID;
    private String recipientID;
    private String message;
    private String type;
    private LocalDateTime timestamp;
    private boolean readStatus;

    //Constructor to initialize a new Notification obj
    public Notification(String recipientID, String message, String type) {
        //Auto-generate a unique 60character ID and append it to "NTF-"
        this.notifID = "NTF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.recipientID = recipientID;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now(); //Automatically capture the exact time of creation
        this.readStatus = false; //By default, a new notification is unread
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
        //if readStatus is false (unread), marker is "* ", otherwise empty string
        String marker = readStatus ? "" : "* ";
        //Combine the formatted date, the read marker, and the message
        return "[" + timestamp.format(FORMATTER) + "] " + marker + message;
    }

    public static Notification claimApproved(String recipientID, String claimID, String itemName) {
        return new Notification(recipientID,
                "Your claim " + claimID + " for " + itemName + " was approved.",
                TYPE_CLAIM_APPROVED);
    }

    //Create a rejection notification
    public static Notification claimRejected(String recipientID, String claimID, String reason) {
        return new Notification(recipientID,
                "Your claim " + claimID + " was rejected. Reason: " + reason,
                TYPE_CLAIM_REJECTED);
    }

    //Create an item verification notification
    public static Notification itemVerified(String recipientID, String itemName) {
        return new Notification(recipientID,
                "Your found item was verified: " + itemName,
                TYPE_ITEM_VERIFIED);
    }
}
