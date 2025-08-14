package domain;

import com.google.gson.annotations.Expose;
import common.enums.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID userId;

    @NotNull(message = "Name is mandatory")
    @Size(min = 2, max = 100,message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String passwordHash;

    @Min(value = 1, message = "Invalid role")
    @Max(value = 4, message = "Invalid role")
    private int roleId;

    private LocalDateTime createdAt;


//    // Default constructor
//    public User() {
//        this.userId = UUID.randomUUID();
//        this.createdAt = LocalDateTime.now();
//    }
//
//    // Constructor with parameters
//    public User(String name, String email, String passwordHash, int roleId) {
//        this();
//        this.name = name;
//        this.email = email;
//        this.passwordHash = passwordHash;
//        this.roleId = roleId;
//    }

    // No-args constructor (for frameworks, should do nothing)
    public User() {}

    // Constructor for creating a new user
    public User(String name, String email, String passwordHash, int roleId) {
        this.userId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();

        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper method to get role enum
    public UserRole getRole() {
        return UserRole.fromId(this.roleId);
    }

    // Helper method to set role using enum
    public void setRole(UserRole role) {
        this.roleId = role.getId();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", createdAt=" + createdAt +
                '}';
    }
}
