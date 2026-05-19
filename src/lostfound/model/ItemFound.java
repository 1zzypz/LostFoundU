package lostfound.model;

import java.time.LocalDate;

public class ItemFound extends Item {

    private String locationFound;
    private LocalDate dateFound;
    private String storeAt;
    private String submitterID;

    public ItemFound(String itemName, String description, Category category,
            String color, String locationFound, LocalDate dateFound,
            String storeAt, String submitterID) {
        super(itemName, description, category, color, Item.STATUS_SUBMITTED);
        this.locationFound = locationFound;
        this.dateFound = dateFound;
        this.storeAt = storeAt;
        this.submitterID = submitterID;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public LocalDate getDateFound() {
        return dateFound;
    }

    public String getStoreAt() {
        return storeAt;
    }

    public String getSubmitterID() {
        return submitterID;
    }

    public void setLocationFound(String locationFound) {
        if (locationFound == null || locationFound.trim().isEmpty()) {
            throw new IllegalArgumentException("Location found cannot be empty.");
        }
        this.locationFound = locationFound.trim();
    }

    public void setDateFound(LocalDate dateFound) {
        if (dateFound == null || dateFound.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date found cannot be in the future.");
        }
        this.dateFound = dateFound;
    }

    public void setStoreAt(String storeAt) {
        this.storeAt = (storeAt != null) ? storeAt.trim() : "";
    }

    @Override
    public String trackStatus() {
        switch (getStatus()) {
            case STATUS_SUBMITTED:
                return "Awaiting admin verification.";
            case STATUS_VERIFIED:
                return "Verified and available for claims.";
            case STATUS_MATCHED:
                return "Matched to a lost report - pending claim approval.";
            case STATUS_CLAIMED:
                return "Successfully claimed by owner.";
            case STATUS_ARCHIVED:
                return "Archived.";
            default:
                return "Status: " + getStatus();
        }
    }

    @Override
    public String getStatusSummary() {
        return getItemID() + " | " + getItemName()
                + " | Found: " + locationFound
                + " | Date: " + dateFound
                + " | Status: " + getStatus();
    }
}
