package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {

    private UUID commentId;
    private UUID issueId;
    private UUID userId;
    private String text;
    private LocalDateTime timestamp;

    public Comment() {}

    public Comment(UUID issueId, UUID userId, String text) {
        this.commentId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();

        this.issueId = issueId;
        this.userId = userId;
        this.text = text;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", issueId=" + issueId +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
