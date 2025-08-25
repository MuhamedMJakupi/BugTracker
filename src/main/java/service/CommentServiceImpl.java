package service;

import common.AbstractService;
import domain.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentServiceImpl extends AbstractService implements  CommentService {

    private boolean userExists(UUID userId) throws SQLException {
        //String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.USER_EXISTS)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean issueExists(UUID issueId) throws SQLException {
        //String sql = "SELECT COUNT(*) FROM issues WHERE issue_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUE_EXISTS)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        }
    }

    private Comment mapComment(ResultSet rs) throws SQLException {

        Comment comment = new Comment();
        comment.setCommentId(UUID.fromString(rs.getString("comment_id")));
        comment.setIssueId(UUID.fromString(rs.getString("issue_id")));
        comment.setUserId(UUID.fromString(rs.getString("user_id")));
        comment.setText(rs.getString("text"));
        comment.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());

        return comment;
    }

    public Comment getCommentById (UUID commentId) throws Exception {
        //String sql = "SELECT * FROM comments WHERE comment_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.COMMENT_BY_ID)) {
            ps.setString(1, commentId.toString());
            ResultSet rs = ps.executeQuery();
            {
                if (rs.next()) {
                    return mapComment(rs);
                }
                return null;
            }
        }
    }

    public List<Comment> getAllComments() throws Exception {
        List<Comment> comments = new ArrayList<>();
        //String sql = "SELECT * FROM comments";
        try(Connection con = getConnection();PreparedStatement ps = con.prepareStatement(SQL.ALL_COMMENTS)) {
            ResultSet rs = ps.executeQuery();
            {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }
        }
        return comments;
    }

    public Comment createComment(Comment comment) throws Exception {
        List<String> errors = comment.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if(!userExists(comment.getUserId())){
            throw new IllegalArgumentException("User not found: " + comment.getUserId());
        }
        if(!issueExists(comment.getIssueId())){
            throw new IllegalArgumentException("Issue not found: " + comment.getIssueId());
        }

        comment.setCommentId(UUID.randomUUID());
        comment.setTimestamp(java.time.LocalDateTime.now());

        //String sql = " INSERT INTO comments (comment_id, issue_id, user_id, text, timestamp) VALUES (?,?,?,?,?)";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_COMMENT)) {
            ps.setString(1, comment.getCommentId().toString());
            ps.setString(2, comment.getIssueId().toString());
            ps.setString(3, comment.getUserId().toString());
            ps.setString(4, comment.getText());
            ps.setTimestamp(5, Timestamp.valueOf(comment.getTimestamp()));
            ps.executeUpdate();
        }
        return comment;
    }

    public void updateComment(Comment comment) throws Exception {
        List<String> errors = comment.validateForUpdate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Comment existingComment = getCommentById(comment.getCommentId());

        if(existingComment == null ){
            throw new IllegalArgumentException("Comment not found: " + comment.getCommentId());
        }
        if(!userExists(comment.getUserId())){
            throw new IllegalArgumentException("User not found: " + comment.getUserId());
        }
        if(!issueExists(comment.getIssueId())){
            throw new IllegalArgumentException("Issue not found: " + comment.getIssueId());
        }

        comment.setTimestamp(java.time.LocalDateTime.now());

        //String sql = "UPDATE comments SET text = ? WHERE comment_id = ?";

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_COMMENT)) {
            ps.setString(1, comment.getText());
            ps.setString(2, comment.getCommentId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteComment(UUID commentId) throws Exception {
        Comment existingComment = getCommentById(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        //String sql = "DELETE FROM comments WHERE comment_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_COMMENT)) {
            ps.setString(1, commentId.toString());
            ps.executeUpdate();
        }
    }

    public List<Comment> getCommentsByIssueId(UUID issueId) throws Exception {
        List<Comment> comments = new ArrayList<>();
        //String sql = "SELECT * FROM comments WHERE issue_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.COMMENT_BY_ISSUE_ID)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comments.add(mapComment(rs));
            }
        }
        return comments;
    }

    public List<Comment> getCommentsByUserId(UUID userId) throws Exception {
        List<Comment> comments = new ArrayList<>();
        //String sql = "SELECT * FROM comments WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.COMMENT_BY_USER_ID)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comments.add(mapComment(rs));
            }
        }
        return comments;
    }

    public static class SQL {
        public static final String USER_EXISTS = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        public static final String ISSUE_EXISTS = "SELECT COUNT(*) FROM issues WHERE issue_id = ?";
        public static final String COMMENT_BY_ID = "SELECT * FROM comments WHERE comment_id = ?";
        public static final String ALL_COMMENTS = "SELECT * FROM comments";
        public static final String CREATE_COMMENT =
                "INSERT INTO comments (comment_id, issue_id, user_id, text, timestamp) VALUES (?,?,?,?,?)";
        public static final String UPDATE_COMMENT ="UPDATE comments SET text = ? WHERE comment_id = ?";
        public static final String DELETE_COMMENT ="DELETE FROM comments WHERE comment_id = ?";
        public static final String COMMENT_BY_ISSUE_ID = "SELECT * FROM comments WHERE issue_id = ?";
        public static final String COMMENT_BY_USER_ID = "SELECT * FROM comments WHERE user_id = ?";
    }
}
