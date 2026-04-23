package model;

import java.time.LocalDateTime;

/**
 * User representation.
 */
public class User {

    private int id;
    private String username;
    private String fullName;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private boolean isAdmin;

    /**
     * Creates an empty user model.
     */
    public User() {
        // Default constructor
    }

    /**
     * Creates a fully populated user model.
     *
     * @param id user primary key
     * @param username username stored in {@code tb_user.username}
     * @param fullName full name stored in {@code tb_user.fullname}
     * @param email email stored in {@code tb_user.email}
     * @param passwordHash password hash stored in {@code tb_user.password_hash}
     * @param createdAt timestamp stored in {@code tb_user.created_at}
     * @param isAdmin whether the account has administrator permissions
     */
    public User(int id, String username, String fullName, String email,
                String passwordHash, LocalDateTime createdAt, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.isAdmin = isAdmin;
    }

    /** @return user admin status */
    public boolean getIsAdmin() { return isAdmin; }

    /** @param isAdmin user admin status */
    public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    /** @return user ID */
    public int getId() { return id; }

    /** @param id user ID */
    public void setId(int id) { this.id = id; }

    /** @return username */
    public String getUsername() { return username; }

    /** @param username username */
    public void setUsername(String username) { this.username = username; }

    /** @return full name */
    public String getFullName() { return fullName; }

    /** @param fullName full name */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /** @return email address */
    public String getEmail() { return email; }

    /** @param email email address */
    public void setEmail(String email) { this.email = email; }

    /** @return password hash */
    public String getPasswordHash() { return passwordHash; }

    /** @param passwordHash password hash */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @return account creation timestamp */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt account creation timestamp */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
