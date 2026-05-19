package lostfound.model;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Item {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_MATCHED = "MATCHED";
    public static final String STATUS_CLAIMED = "CLAIMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";

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
        this.status = status;
    }

    public abstract String trackStatus();

    public abstract String getStatusSummary();

    public boolean matchesKeyword(String keyword) {
        String kw = keyword.toLowerCase();
        return itemName.toLowerCase().contains(kw)
                || description.toLowerCase().contains(kw)
                || category.getDisplayName().toLowerCase().contains(kw)
                || color.toLowerCase().contains(kw);
    }
}
