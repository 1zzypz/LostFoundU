package lostfound.model;

import java.time.LocalDate;
import java.util.UUID;

public class RegisteredUser extends User {

    private String registeredUserID;
    private String address;

    public RegisteredUser(String name, String email, String phone,
            String password, String address) {
        super(name, email, phone, password);
        this.registeredUserID = "RU-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.address = address;
    }

    public String getRegisteredUserID() {
        return registeredUserID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address.trim() : ""; //address.trim();remove spaces, "" assign blank string
    }

    public ItemLost reportLostItem(String itemName, String description,
            Category category, String color, String locationLost, LocalDate dateLost) {
        return new ItemLost(itemName, description, category, color,
                locationLost, dateLost, getUserID());
    }

    public ItemFound submitFoundItem(String itemName, String description,
            Category category, String color, String locationFound,
            LocalDate dateFound, String storeAt) {
        return new ItemFound(itemName, description, category, color,
                locationFound, dateFound, storeAt, getUserID());
    }

    public ClaimRequest submitClaim(String foundItemID, String lostReportID,
            String proofDescription) {
        return new ClaimRequest(getUserID(), foundItemID, lostReportID, proofDescription);
    }

    @Override
    public String getRole() {
        return "User";
    }

    @Override
    public String getSummary() {
        return registeredUserID + " | " + getName()
                + " | " + getEmail()
                + " | " + getPhone();
    }
}
