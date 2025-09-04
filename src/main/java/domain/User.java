package domain;

import common.AbstractEntity;
import common.enums.UserRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class User extends AbstractEntity {

    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private int roleId;
    private String createdAt;

    public User() {}

    public User(String name, String email, String passwordHash, int roleId) {
        setUserId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now().toString();

        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
    }

    public UUID getUserId() { return getId(); }
    public void setUserId(UUID userId) { setId(userId); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getRoleString() { return role; }
    public void setRoleString(String role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is mandatory");
        } else if (name.trim().length() < 2 || name.trim().length() > 100) {
            errors.add("Name must be between 2 and 100 characters");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is mandatory");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Email must be valid");
        }

        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            errors.add("Password is mandatory");
        } else if (passwordHash.length() < 8) {
            errors.add("Password must be at least 8 characters");
        } else if (passwordHash.length() > 255) {
            errors.add("Password hash too long");
        }

        boolean hasRole = role != null && !role.trim().isEmpty();
        boolean hasRoleId = roleId > 0;
        if (hasRole == hasRoleId) {
            errors.add("Exactly one of 'role' or 'roleId' must be provided, not both");
        } else {
            if (hasRole) {
                try {
                    UserRole.fromName(role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid role: " + role);
                }
            } else {
                if (roleId < 1 || roleId > 4) {
                    errors.add("Invalid roleId: must be between 1 and 4");
                }
            }
        }
        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getUserId() != null) {
            errors.add("User ID should not be provided for new users");
        }

        if (createdAt != null) {
            errors.add("Created timestamp should not be provided for new users");
        }
        return errors;
    }
}
