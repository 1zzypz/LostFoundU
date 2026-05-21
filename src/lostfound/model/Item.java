package lostfound.model;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Item {

    //Constants - fixed value that will be the same or never change
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_MATCHED = "MATCHED";
    public static final String STATUS_CLAIMED = "CLAIMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";

    //Encapsulation - variables that cannot be access directly outside the Item class
    //In order to access it, other classes need to use getters and setters
    private String itemID;
    private String itemName;
    private String description;
    private Category category;
    private String color;
    private String status;
    private LocalDate submittedDate;

    public Item(String itemName, String description, Category category,
            String color, String status) {
        this.itemID = "ITM-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.color = color;
        this.status = status;
        this.submittedDate = LocalDate.now();
    }
    
    //Encapsulation - this method validate the status values, if the status is null then the values is pending.
    public String validateStatus(String status){
        if(status == null || status.trim().isEmpty()){
            return STATUS_PENDING;
        }
        
        String upperStatus = status.toUpperCase();
        switch(upperStatus){
            case STATUS_PENDING:
            case STATUS_SUBMITTED:
            case STATUS_VERIFIED:
            case STATUS_MATCHED:
            case STATUS_CLAIMED:
            case STATUS_CANCELLED:
            case STATUS_ARCHIVED:
                return upperStatus;
            default: 
                return STATUS_PENDING;
        }
    }

    //Getter Encapsulation - allow other classes to read the private data
    public String getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public String getColor() {
        return color;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    //Setter Encapsulation - Allow other classes to change the private data
    public void setItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        this.itemName = itemName.trim();
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        this.category = category;
    }

    public void setColor(String color) {
        this.color = (color != null) ? color.trim() : "";
    }

    public void setStatus(String status) {
        this.status = validateStatus(status);
    }

    //Polymorphism - force the child classes to create their own version of the trackStatus() method
    //Same method name with different behaviour in each child class
    public abstract String trackStatus();

    //Polymorphism - force the child classes to create their own version of the getStatusSummary() method
    //Same method name with different behaviour in each child class
    public abstract String getStatusSummary();

    public boolean matchesKeyword(String keyword) {
        String kw = keyword.toLowerCase();
        return itemName.toLowerCase().contains(kw)
                || description.toLowerCase().contains(kw)
                || category.getDisplayName().toLowerCase().contains(kw)
                || color.toLowerCase().contains(kw);
    }
}
