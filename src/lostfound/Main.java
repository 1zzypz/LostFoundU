package lostfound;

import lostfound.model.Category;
import lostfound.model.ClaimRequest;
import lostfound.model.Item;
import lostfound.model.ItemFound;
import lostfound.model.ItemLost;
import lostfound.model.Notification;
import lostfound.model.RegisteredUser;
import lostfound.model.Staff;
import lostfound.model.User;
import lostfound.session.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CampusLostFoundSystem system = new CampusLostFoundSystem();
        system.run();
    }
}

class CampusLostFoundSystem {

    private static final int MAX_USERS = 30;
    private static final int MAX_STAFF = 10;
    private static final int MAX_LOST = 50;
    private static final int MAX_FOUND = 50;
    private static final int MAX_CLAIMS = 50;
    private static final int MAX_NOTIFICATIONS = 100;

    private static final int USER_COUNT = 0;
    private static final int STAFF_COUNT = 1;
    private static final int LOST_COUNT = 2;
    private static final int FOUND_COUNT = 3;
    private static final int CLAIM_COUNT = 4;
    private static final int NOTIFICATION_COUNT = 5;

    private final Scanner scanner = new Scanner(System.in);
    private final SessionManager session = SessionManager.getInstance();

    private final RegisteredUser[] users = new RegisteredUser[MAX_USERS];
    private final Staff[] staffMembers = new Staff[MAX_STAFF];
    private final ItemLost[] lostItems = new ItemLost[MAX_LOST];
    private final ItemFound[] foundItems = new ItemFound[MAX_FOUND];
    private final ClaimRequest[] claims = new ClaimRequest[MAX_CLAIMS];
    private final Notification[] notifications = new Notification[MAX_NOTIFICATIONS];

    private final int[] counts = new int[6];

    public CampusLostFoundSystem() {
        seedData();
    }

    public void run() {
        boolean running = true;
        printHeader();
        while (running) {
            printMainMenu();
            int choice = readInt("Choose: ");
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    browseFoundItems();
                    break;
                case 4:
                    searchItems();
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void login() {
        System.out.println("\n-- Login --");
        String email = readLine("Email: ").trim().toLowerCase();
        String password = readLine("Password: ");

        User user = findUserByEmail(email); //UPCASTING 
        if (user == null || !user.verifyPassword(password)) {
            System.out.println("Login failed. Check email and password.");
            return;
        }

        session.login(user);
        System.out.println("Welcome, " + user.getName() + " (" + user.getRole() + ").");
        if (user instanceof Staff) {
            adminMenu((Staff) user); //DOWNCASTING 
        } else {
            userMenu((RegisteredUser) user); //DOWNCASTING
        }
    }

    private void registerUser() {
        if (counts[USER_COUNT] >= MAX_USERS) {
            System.out.println("User storage is full.");
            return;
        }

        System.out.println("\n-- Register User --");
        String name = readRequired("Name: ");
        String email = readRequired("Email: ").trim().toLowerCase();
        if (!email.contains("@")) {
            System.out.println("Invalid email.");
            return;
        }
        if (findUserByEmail(email) != null) {
            System.out.println("Email already exists.");
            return;
        }

        String phone = readRequired("Phone: ");
        String password = readRequired("Password at least 6 characters: ");
        if (password.length() < 6) {
            System.out.println("Password too short.");
            return;
        }
        String address = readLine("Address: ");

        users[counts[USER_COUNT]++] = new RegisteredUser(name, email, phone, password, address);
        System.out.println("Registration successful. You can now log in.");
    }

    private void userMenu(RegisteredUser user) {
        boolean active = true;
        while (active && session.isLoggedIn()) {
            System.out.println("\n-- User Menu --");
            System.out.println("1. Report lost item");
            System.out.println("2. Submit found item");
            System.out.println("3. Browse found items");
            System.out.println("4. Search all items");
            System.out.println("5. Submit claim");
            System.out.println("6. View my claims");
            System.out.println("7. View notifications");
            System.out.println("0. Logout");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    reportLostItem(user);
                    break;
                case 2:
                    submitFoundItem(user);
                    break;
                case 3:
                    browseFoundItems();
                    break;
                case 4:
                    searchItems();
                    break;
                case 5:
                    submitClaim(user);
                    break;
                case 6:
                    viewClaimsForUser(user.getUserID());
                    break;
                case 7:
                    viewNotifications(user.getUserID());
                    break;
                case 0:
                    session.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void adminMenu(Staff staff) {
        boolean active = true;
        while (active && session.isLoggedIn()) {
            System.out.println("\n-- Admin Menu --");
            System.out.println("1. View found items");
            System.out.println("2. View lost reports");
            System.out.println("3. Verify found item");
            System.out.println("4. Review claims");
            System.out.println("5. Generate report");
            System.out.println("6. View registered users");
            System.out.println("0. Logout");
            int choice = readInt("Choose: ");

            switch (choice) {
                case 1:
                    browseFoundItems();
                    break;
                case 2:
                    browseLostReports();
                    break;
                case 3:
                    verifyFoundItem(staff);
                    break;
                case 4:
                    reviewClaims(staff);
                    break;
                case 5:
                    generateReport(staff);
                    break;
                case 6:
                    viewRegisteredUsers();
                    break;
                case 0:
                    session.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void reportLostItem(RegisteredUser user) {
        if (counts[LOST_COUNT] >= MAX_LOST) {
            System.out.println("Lost item storage is full.");
            return;
        }

        System.out.println("\n-- Report Lost Item --");
        String name = readRequired("Item name: ");
        String description = readLine("Description: ");
        Category category = chooseCategory();
        String color = readLine("Color: ");
        String location = readRequired("Location lost: ");
        LocalDate date = readDate("Date lost (yyyy-MM-dd): ");

        ItemLost item = user.reportLostItem(name, description, category, color, location, date);
        lostItems[counts[LOST_COUNT]++] = item;
        System.out.println("Lost report submitted. ID: " + item.getItemID());
    }

    private void submitFoundItem(RegisteredUser user) {
        if (counts[FOUND_COUNT] >= MAX_FOUND) {
            System.out.println("Found item storage is full.");
            return;
        }

        System.out.println("\n-- Submit Found Item --");
        String name = readRequired("Item name: ");
        String description = readLine("Description: ");
        Category category = chooseCategory();
        String color = readLine("Color: ");
        String location = readRequired("Location found: ");
        LocalDate date = readDate("Date found (yyyy-MM-dd): ");
        String storeAt = readLine("Stored at: ");

        ItemFound item = user.submitFoundItem(name, description, category, color, location, date, storeAt);
        foundItems[counts[FOUND_COUNT]++] = item;
        System.out.println("Found item submitted. ID: " + item.getItemID());
    }

    private void browseFoundItems() {
        System.out.println("\n-- Found Items --");
        if (counts[FOUND_COUNT] == 0) {
            System.out.println("No found items yet.");
            return;
        }
        for (int i = 0; i < counts[FOUND_COUNT]; i++) {
            System.out.println((i + 1) + ". " + foundItems[i].getStatusSummary());
        }
    }

    private void browseLostReports() {
        System.out.println("\n-- Lost Reports --");
        if (counts[LOST_COUNT] == 0) {
            System.out.println("No lost reports yet.");
            return;
        }
        for (int i = 0; i < counts[LOST_COUNT]; i++) {
            System.out.println((i + 1) + ". " + lostItems[i].getStatusSummary());
        }
    }

    private void searchItems() {
        String keyword = readRequired("\nSearch keyword: ").toLowerCase();
        int matches = 0;

        System.out.println("-- Matching Lost Reports --");
        for (int i = 0; i < counts[LOST_COUNT]; i++) {
            if (lostItems[i].matchesKeyword(keyword)) {
                System.out.println(lostItems[i].getStatusSummary());
                matches++;
            }
        }

        System.out.println("-- Matching Found Items --");
        for (int i = 0; i < counts[FOUND_COUNT]; i++) {
            if (foundItems[i].matchesKeyword(keyword)) {
                System.out.println(foundItems[i].getStatusSummary());
                matches++;
            }
        }

        if (matches == 0) {
            System.out.println("No matches for '" + keyword + "'.");
        }
    }

    private void submitClaim(RegisteredUser user) {
        if (counts[CLAIM_COUNT] >= MAX_CLAIMS) {
            System.out.println("Claim storage is full.");
            return;
        }

        browseFoundItems();
        String foundID = readRequired("Found item ID to claim: ");
        ItemFound foundItem = findFoundByID(foundID);
        if (foundItem == null) {
            System.out.println("Found item not found.");
            return;
        }

        String lostID = readLine("Your lost report ID (optional): ");
        String proof = readRequired("Proof of ownership: ");
        ClaimRequest claim = user.submitClaim(foundItem.getItemID(), lostID.trim().isEmpty() ? null : lostID, proof);
        claims[counts[CLAIM_COUNT]++] = claim;
        foundItem.setStatus(Item.STATUS_MATCHED);
        System.out.println("Claim submitted. Claim ID: " + claim.getClaimID());
    }

    private void viewClaimsForUser(String userID) {
        System.out.println("\n-- My Claims --");
        boolean found = false;
        for (int i = 0; i < counts[CLAIM_COUNT]; i++) {
            if (claims[i].getUserID().equals(userID)) {
                System.out.println(claims[i].getTableSummary());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No claims submitted.");
        }
    }

    private void viewNotifications(String userID) {
        System.out.println("\n-- Notifications --");
        boolean found = false;
        for (int i = 0; i < counts[NOTIFICATION_COUNT]; i++) {
            if (notifications[i].getRecipientID().equals(userID)) {
                System.out.println(notifications[i].getNotifDetails());
                notifications[i].markAsRead();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No notifications.");
        }
    }

    private void verifyFoundItem(Staff staff) {
        browseFoundItems();
        String itemID = readRequired("Found item ID to verify: ");
        ItemFound item = findFoundByID(itemID);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (staff.verifyItem(item)) {
            addNotification(Notification.itemVerified(item.getSubmitterID(), item.getItemName()));
            System.out.println("Item verified.");
        } else {
            System.out.println("Only SUBMITTED items can be verified.");
        }
    }

    private void reviewClaims(Staff staff) {
        System.out.println("\n-- Claims --");
        if (counts[CLAIM_COUNT] == 0) {
            System.out.println("No claims yet.");
            return;
        }

        for (int i = 0; i < counts[CLAIM_COUNT]; i++) {
            System.out.println((i + 1) + ". " + claims[i].getTableSummary());
        }

        String claimID = readRequired("Claim ID to review: ");
        ClaimRequest claim = findClaimByID(claimID);
        if (claim == null) {
            System.out.println("Claim not found.");
            return;
        }

        System.out.println(claim.getClaimDetails());
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        System.out.println("0. Cancel");
        int choice = readInt("Choose: ");
        ItemFound foundItem = findFoundByID(claim.getFoundItemID());

        if (choice == 1 && foundItem != null && staff.approveClaim(claim, foundItem)) {
            addNotification(Notification.claimApproved(claim.getUserID(), claim.getClaimID(), foundItem.getItemName()));
            System.out.println("Claim approved.");
        } else if (choice == 2 && staff.rejectClaim(claim, readRequired("Reason: "))) {
            addNotification(Notification.claimRejected(claim.getUserID(), claim.getClaimID(), claim.getRejectionReason()));
            System.out.println("Claim rejected.");
        } else {
            System.out.println("No change made.");
        }
    }

    private void generateReport(Staff staff) {
        Item[] allItems = new Item[counts[LOST_COUNT] + counts[FOUND_COUNT]];
        ClaimRequest[] allClaims = new ClaimRequest[counts[CLAIM_COUNT]];

        int index = 0;
        for (int i = 0; i < counts[LOST_COUNT]; i++) {
            allItems[index++] = lostItems[i]; //UPCASTING 
        }
        for (int i = 0; i < counts[FOUND_COUNT]; i++) {
            allItems[index++] = foundItems[i]; //UPCASTING 
        }
        for (int i = 0; i < counts[CLAIM_COUNT]; i++) {
            allClaims[i] = claims[i];
        }

        staff.generateReport(Arrays.asList(allItems), Arrays.asList(allClaims));
    }

    private void viewRegisteredUsers() {
        System.out.println("\n-- Registered Users --");
        for (int i = 0; i < counts[USER_COUNT]; i++) {
            System.out.println((i + 1) + ". " + users[i].getSummary());
        }
    }

    private Category chooseCategory() {
        Category[] categories = Category.values();
        System.out.println("Categories:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i].getDisplayName());
        }

        int choice = readInt("Category: ");
        if (choice < 1 || choice > categories.length) {
            System.out.println("Invalid category. Using Others.");
            return Category.OTHERS;
        }
        return categories[choice - 1];
    }

    private User findUserByEmail(String email) {
        String normalized = email.trim().toLowerCase();
        for (int i = 0; i < counts[USER_COUNT]; i++) {
            if (users[i].getEmail().equalsIgnoreCase(normalized)) {
                return users[i];
            }
        }
        for (int i = 0; i < counts[STAFF_COUNT]; i++) {
            if (staffMembers[i].getEmail().equalsIgnoreCase(normalized)) {
                return staffMembers[i];
            }
        }
        return null;
    }

    private ItemFound findFoundByID(String itemID) {
        String normalized = itemID.trim();
        for (int i = 0; i < counts[FOUND_COUNT]; i++) {
            if (foundItems[i].getItemID().equalsIgnoreCase(normalized)) {
                return foundItems[i];
            }
        }
        return null;
    }

    private ClaimRequest findClaimByID(String claimID) {
        String normalized = claimID.trim();
        for (int i = 0; i < counts[CLAIM_COUNT]; i++) {
            if (claims[i].getClaimID().equalsIgnoreCase(normalized)) {
                return claims[i];
            }
        }
        return null;
    }

    private void addNotification(Notification notification) {
        if (counts[NOTIFICATION_COUNT] < MAX_NOTIFICATIONS) {
            notifications[counts[NOTIFICATION_COUNT]++] = notification;
        }
    }

    private String readRequired(String prompt) {
        String value;
        do {
            value = readLine(prompt).trim();
            if (value.isEmpty()) {
                System.out.println("This field is required.");
            }
        } while (value.isEmpty());
        return value;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String input = readRequired(prompt);
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("Date cannot be in the future.");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Use yyyy-MM-dd format.");
            }
        }
    }

    private void seedData() {
        users[counts[USER_COUNT]++] = new RegisteredUser(
                "AARON AZIZ", "student@adab.umpsa.edu.my", "01115771808",
                "user123", "DHUAM");
        staffMembers[counts[STAFF_COUNT]++] = new Staff(
                "JHEPA", "jhepa@adab.umpsa.edu.my", "0123456789",
                "admin123", "Student Affairs");

        RegisteredUser demo = users[0];
        lostItems[counts[LOST_COUNT]++] = demo.reportLostItem(
                "Blue Wallet", "Dark blue wallet with student ID inside",
                Category.WALLET, "Blue", "Library Level 2",
                LocalDate.now().minusDays(2));
        ItemFound bottle = demo.submitFoundItem(
                "Water Bottle", "Silver bottle with a university sticker",
                Category.WATER_BOTTLE, "Silver", "Cafeteria",
                LocalDate.now().minusDays(1), "Student Affairs Counter");
        bottle.setStatus(Item.STATUS_VERIFIED);
        foundItems[counts[FOUND_COUNT]++] = bottle;
    }

    private void printHeader() {
        System.out.println("======================================");
        System.out.println(" UMPSA CAMPUS LOST & FOUND SYSTEM");
        System.out.println("======================================");
        System.out.println("Demo user : student@adab.umpsa.edu.my / user123");
        System.out.println("Demo admin: jhepa@adab.umpsa.edu.my / admin123");
    }

    private void printMainMenu() {
        System.out.println("\n-- Main Menu --");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Browse found items");
        System.out.println("4. Search items");
        System.out.println("0. Exit");
    }
}
