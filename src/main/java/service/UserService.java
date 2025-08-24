package service;

import common.AbstractService;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService extends AbstractService {

    private final UserValidationService validationService = new UserValidationService();

    public User createUser(User user) throws Exception {
        List<String> errors = validationService.validateUserForCreation(user);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setUserId(UUID.randomUUID());
        user.setCreatedAt(java.time.LocalDateTime.now());

        //String sql = "INSERT INTO users (user_id, name, email, password_hash, role_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_USER)) {
            ps.setString(1, user.getUserId().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setInt(5, user.getRoleId());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));
            ps.executeUpdate();
        }
        return user;
    }

    public void updateUser(User user) throws Exception {
        List<String> errors = validationService.validateUserForUpdate(user);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        User existingUser = getUserById(user.getUserId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (emailExistsExcludingUser(user.getEmail(), user.getUserId())) {
            throw new IllegalArgumentException("Email already exists");
        }

        //String sql = "UPDATE users SET name=?, email=?, password_hash=?, role_id=? WHERE user_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_USER)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleId());
            ps.setString(5, user.getUserId().toString());
            ps.executeUpdate();
        }
    }

    public User getUserById(UUID id) throws Exception {
        //String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.USER_BY_ID)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        }
    }

    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        //String sql = "SELECT * FROM users";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_USERS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public void deleteUser(UUID id) throws Exception {
        User existingUser = getUserById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        //String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_USER)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    private boolean emailExists(String email) throws Exception {
        //String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.EMAIL_EXISTS)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Helper: check if email exists excluding specific user (for updates)
    private boolean emailExistsExcludingUser(String email, UUID excludeUserId) throws Exception {
        //String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.EMAIL_EXISTS_EXCLUDING_USER)) {
            ps.setString(1, email);
            ps.setString(2, excludeUserId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Helper: map User from ResultSet
    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRoleId(rs.getInt("role_id"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
    public static class SQL {
        public static final String EMAIL_EXISTS_EXCLUDING_USER = """
                SELECT COUNT(*) 
                FROM users 
                WHERE email = ? AND user_id != ?
                """;
        public static final String EMAIL_EXISTS = "SELECT COUNT(*) FROM users WHERE email = ?";

        public static final String DELETE_USER = "DELETE FROM users WHERE user_id=?";

        public static final String ALL_USERS = "SELECT * FROM users";

        public static final String USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

        public static final String UPDATE_USER = """
        UPDATE users 
        SET name=?, email=?, password_hash=?, role_id=? 
        WHERE user_id=?
        """;

        public static final String CREATE_USER = """
        INSERT INTO users (user_id, name, email, password_hash, role_id, created_at) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    }
}
