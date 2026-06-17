package lostfound.model;

import java.time.LocalDate;

public class ItemLost extends Item {

    private String locationLost;
    private LocalDate dateLost;
    private String reporterID;

    public ItemLost(String itemName, String description, Category category,
            String color, String locationLost, LocalDate dateLost,
            String reporterID) {
        super(itemName, description, category, color, Item.STATUS_PENDING);
        this.locationLost = locationLost;
        this.dateLost = dateLost;
        this.reporterID = reporterID;
    }

    public String getLocationLost() {
        return locationLost;
    }

    public LocalDate getDateLost() {
        return dateLost;
    }

    public String getReporterID() {
        return reporterID;
    }

    public void setLocationLost(String locationLost) {
        if (locationLost == null || locationLost.trim().isEmpty()) {
            throw new IllegalArgumentException("Location lost cannot be empty.");
        }
        this.locationLost = locationLost.trim();
    }

    public void setDateLost(LocalDate dateLost) {
        if (dateLost == null || dateLost.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date lost cannot be in the future.");
        }
        this.dateLost = dateLost;
    }

    @Override
    public String trackStatus() {
        switch (getStatus()) {
            case STATUS_PENDING:
                return "Waiting for staff review.";
            case STATUS_VERIFIED:
                return "Active lost report.";
            case STATUS_MATCHED:
                return "Possible match found.";
            case STATUS_CLAIMED:
                return "Recovered.";
            case STATUS_CANCELLED:
                return "Cancelled.";
            default:
                return "Status: " + getStatus();
        }
    }

    @Override
    public String getStatusSummary() {
        return getItemID() + " | " + getItemName()
                + " | Lost: " + locationLost
                + " | Date: " + dateLost
                + " | Status: " + getStatus();
    }
}
