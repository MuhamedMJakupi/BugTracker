package service;

import common.AbstractService;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService extends AbstractService {

    // Create
    public User createUser(User user) throws Exception {
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String sql = "INSERT INTO users (user_id, name, email, password_hash, role_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
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

    // Read
    public User getUserById(UUID id) throws Exception {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(UUID.fromString(rs.getString("user_id")));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRoleId(rs.getInt("role_id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return user;
            }
            return null;
        }
    }

    // Read all
    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(UUID.fromString(rs.getString("user_id")));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRoleId(rs.getInt("role_id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                users.add(user);
            }
        }
        return users;
    }

    // Update
    public void updateUser(User user) throws Exception {
        String sql = "UPDATE users SET name=?, email=?, password_hash=?, role_id=? WHERE user_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleId());
            ps.setString(5, user.getUserId().toString());
            ps.executeUpdate();
        }
    }

    // Delete
    public void deleteUser(UUID id) throws Exception {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    // Helper check
    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
