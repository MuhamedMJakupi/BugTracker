package service;

import common.AbstractService;
import common.DBValidationUtils;
import common.PasswordUtils;
import common.enums.UserRole;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserServiceImpl extends AbstractService implements UserService {

    private final DBValidationUtils validationUtils = new DBValidationUtils();

    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));

        int roleId = rs.getInt("role_id");
        user.setRoleId(roleId);
        try {
            user.setRoleString(UserRole.fromId(roleId).getName());
        } catch (IllegalArgumentException e) {
            user.setRoleString("UNKNOWN");
        }
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toString());
        return user;
    }

    private void convertRoleStringToId(User user) {
        if (user.getRoleString() != null && !user.getRoleString().trim().isEmpty()) {
            UserRole role = UserRole.fromName(user.getRoleString().toUpperCase());
            user.setRoleId(role.getId());
        }
    }

    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_USERS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public User getUserById(UUID id) throws Exception {
        validationUtils.validateUserExists(id, "User");
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.USER_BY_ID)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        }
    }

    public User createUser(User user) throws Exception {
        List<String> errors = user.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        convertRoleStringToId(user);
        validationUtils.validateEmailUnique(user.getEmail());

        String hashedPassword = PasswordUtils.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        user.setUserId(UUID.randomUUID());
        user.setCreatedAt(java.time.LocalDateTime.now().toString());

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_USER)) {
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setInt(5, user.getRoleId());
            LocalDateTime ltd = LocalDateTime.parse(user.getCreatedAt());
            ps.setTimestamp(6, Timestamp.valueOf(ltd));
            ps.executeUpdate();
        }
        return user;
    }

    public void updateUser(User user) throws Exception {
        List<String> errors = user.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        convertRoleStringToId(user);
        User existingUser = getUserById(user.getUserId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        validationUtils.validateEmailUniqueForUpdate(user.getEmail(),user.getUserId());

        if (user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty()) {
            String hashedPassword = PasswordUtils.hashPassword(user.getPasswordHash());
            user.setPasswordHash(hashedPassword);
        } else {
            user.setPasswordHash(existingUser.getPasswordHash());
        }

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_USER)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleId());
            ps.setString(5, user.getUserId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteUser(UUID id) throws Exception {
        validationUtils.validateUserExists(id, "User");
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_USER)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public boolean authenticateUser(String email, String plainPassword) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.USER_AUTH)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return PasswordUtils.checkPassword(plainPassword, storedHash);
            }
            return false;
        }
    }
    public List<Map<String, Object>> getAllRoles() throws Exception {
        List<Map<String, Object>> roles = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.GET_ALL_ROLES)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(Map.of(
                        "id", rs.getInt("role_id"),
                        "name", rs.getString("name")
                ));
            }
        }
        return roles;
    }

    public static class SQL {

        public static final String ALL_USERS = "SELECT * FROM users";
        public static final String USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
        public static final String CREATE_USER = """
        INSERT INTO users (user_id, name, email, password_hash, role_id, created_at) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;
        public static final String UPDATE_USER = """
        UPDATE users 
        SET name=?, email=?, password_hash=?, role_id=? 
        WHERE user_id=?
        """;
        public static final String DELETE_USER = "DELETE FROM users WHERE user_id=?";

        public static final String USER_AUTH = "SELECT password_hash FROM users WHERE email = ?";

        public static final String GET_ALL_ROLES = "SELECT role_id, name FROM user_roles ORDER BY role_id";
    }
}
