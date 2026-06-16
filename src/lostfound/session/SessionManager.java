package lostfound.session;

import lostfound.model.User;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private User currentUser;
    private String accountID;
    private String name;
    private String email;
    private String role;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void login(User user) {
        currentUser = user;
        if (user != null) {
            accountID = user.getUserID();
            name = user.getName();
            email = user.getEmail();
            role = user.getRole();
        }
    }

    public void loginDatabaseUser(String accountID, String name, String email) {
        currentUser = null;
        this.accountID = accountID;
        this.name = name;
        this.email = email;
        this.role = "User";
    }

    public void loginDatabaseStaff(String accountID, String name, String email) {
        currentUser = null;
        this.accountID = accountID;
        this.name = name;
        this.email = email;
        this.role = "Staff";
    }

    public void logout() {
        currentUser = null;
        accountID = null;
        name = null;
        email = null;
        role = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null || accountID != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
