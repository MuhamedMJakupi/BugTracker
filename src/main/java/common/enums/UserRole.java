// domain/UserRole.java
package common.enums;

public enum UserRole {
    ADMIN(1, "ADMIN"),
    PROJECT_MANAGER(2, "PROJECT_MANAGER"),
    DEVELOPER(3, "DEVELOPER"),
    TESTER(4, "TESTER");

    private final int id;
    private final String name;

    UserRole(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    // Utility methods
    public static UserRole fromId(int id) {
        for (UserRole role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role ID: " + id);
    }

    public static UserRole fromName(String name) {
        for (UserRole role : values()) {
            if (role.name.equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role name: " + name);
    }

    // Permission checks
    public boolean canManageUsers() {
        return this == ADMIN;
    }

    public boolean canManageProjects() {
        return this == ADMIN || this == PROJECT_MANAGER;
    }

    public boolean canCreateIssues() {
        return true; // All roles can create issues
    }

    public boolean canAssignIssues() {
        return this == ADMIN || this == PROJECT_MANAGER;
    }

    public boolean canDeleteIssues() {
        return this == ADMIN || this == PROJECT_MANAGER;
    }
}
