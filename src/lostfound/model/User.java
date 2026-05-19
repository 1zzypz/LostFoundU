package lostfound.model;

import java.time.LocalDate;
import java.util.UUID;

public abstract class User {
    //Encapsulation principle. fields aare private, object is declared as private
    private String userID;
    private String name;
    private String email;
    private String phone;
    private String password;
    private LocalDate registrationDate;

    public User(String name, String email, String phone, String password) {
        this.userID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.registrationDate = LocalDate.now();
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email.trim().toLowerCase();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }
        this.phone = phone.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        this.password = password;
    }

    public boolean verifyPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    public abstract String getRole();

    public abstract String getSummary();

    @Override
    public String toString() {
        return "[" + getRole() + "] " + name + " (" + email + ")";
    }
}
