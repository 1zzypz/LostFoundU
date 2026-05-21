package lostfound.model;

import java.time.LocalDate;

//Inheritance - ItemLost class inherit to Item class
public class ItemLost extends Item {

    //Encapsulation - private and new fields that only ItemLost class have
    private String locationLost;
    private LocalDate dateLost; //Composition - date that belongs to the item
    private String reporterID; // Aggregation - ID that refers to a User

    //Inheritance - calls the fields from parent(Item) class
    public ItemLost(String itemName, String description, Category category,
            String color, String locationLost, LocalDate dateLost,
            String reporterID) {
        // super() calls the constructor in parent class which is Item class
        super(itemName, description, category, color, Item.STATUS_PENDING);
        this.locationLost = locationLost;
        this.dateLost = dateLost;
        this.reporterID = reporterID;
    }

    //Getter Encapsulation - it let other classes to access the private data
    public String getLocationLost() {
        return locationLost;
    }

    public LocalDate getDateLost() {
        return dateLost;
    }

    public String getReporterID() {
        return reporterID;
    }

    //Setter Encapsulation - let other classes to change the private fields or data
    public void setLocationLost(String locationLost) {
        if (locationLost == null || locationLost.trim().isEmpty()) {
            throw new IllegalArgumentException("Location lost cannot be empty.");
        }
        this.locationLost = locationLost.trim();
    }

    public void setDateLost(LocalDate dateLost) {
        if (dateLost == null) {
            throw new IllegalArgumentException("Date lost cannot be null.");
        }
        if (dateLost.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date lost cannot be in the future.");
        }
        this.dateLost = dateLost;
    }
    
     public void setReporterID(String reporterID){
        if (reporterID == null || reporterID.trim().isEmpty()){
            throw new IllegalArgumentException("Reporter ID cannot be empty.");
        }
        this.reporterID = reporterID.trim();
    }

    //Polymorphism - item lost version of the parent class abstract 'trackStatus()' method
    @Override
    public String trackStatus() {
        switch (getStatus()) {
            case STATUS_PENDING:
                return "Waiting for staff review.";
            case STATUS_SUBMITTED:
                return "Lost report submitted, awaiting verification.";
            case STATUS_VERIFIED:
                return "Active lost report.";
            case STATUS_MATCHED:
                return "Possible match found.";
            case STATUS_CLAIMED:
                return "Item has been recovered and claimed";
            case STATUS_CANCELLED:
                return "Lost report has been cancelled.";
            case STATUS_ARCHIVED:
                return "Report is archived.";
            default:
                return "Status: " + getStatus();
        }
    }

    //Polymorphism - override the abstract parent to print specific lost report including locationLost, and reporterID attributes.
    @Override
    public String getStatusSummary() {
        return getItemID() + " | " + getItemName()
                + " | Lost: " + locationLost
                + " | Date: " + dateLost
                + " | Reporter: " + reporterID
                + " | Status: " + getStatus();
    }
}
