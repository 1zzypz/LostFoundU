package lostfound.model;

import java.time.LocalDate;

//Inheritance: ItemFound inherits attributes and methods from the superclass Item
public class ItemFound extends Item {

    //Encapsulation: private variables to hide data
    private String locationFound;
    private LocalDate dateFound;
    private String storeAt;
    private String submitterID;

    //Constructor to initialize new ItemFound obj
    public ItemFound(String itemName, String description, Category category,
            String color, String locationFound, LocalDate dateFound,
            String storeAt, String submitterID) {
        //call to superclass (Item) constructor to initialize inherited attributes
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
        
        //validation to ensure field is not null or empty before setting
        if (locationFound == null || locationFound.trim().isEmpty()) {
            throw new IllegalArgumentException("Location found cannot be empty.");
        }
        this.locationFound = locationFound.trim(); //remove leading/trailing spaces
    }

    public void setDateFound(LocalDate dateFound) {
        //validation to ensure the date is provided and isn't set in the future
        if (dateFound == null || dateFound.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date found cannot be in the future.");
        }
        this.dateFound = dateFound;
    }

    public void setStoreAt(String storeAt) {
        //if storeAt is not null, trim it; otherwise, assign an empty string
        this.storeAt = (storeAt != null) ? storeAt.trim() : "";
    }

    //Polymorhism: Overriding the trackStatus method defined in the parent class Item
    @Override
    public String trackStatus() {
        //run method getStatus() inherited from parent class to check current state
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

    //Polymorphism: Overriding the getStatusSummary method from parent class Item
    @Override
    public String getStatusSummary() {
        //calling inherited getter methods (getItemID, getItemName, getStatus) to build the string
        return getItemID() + " | " + getItemName()
                + " | Found: " + locationFound
                + " | Date: " + dateFound
                + " | Status: " + getStatus();
    }
}
