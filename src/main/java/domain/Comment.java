package domain;

import common.AbstractEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Comment extends AbstractEntity {

    private UUID issueId;
    private UUID userId;
    private String text;
    private LocalDateTime timestamp;

    public Comment() {}

    public Comment(UUID issueId, UUID userId, String text) {

        setCommentId(UUID.randomUUID());
        this.timestamp = LocalDateTime.now();

        this.issueId = issueId;
        this.userId = userId;
        this.text = text;
    }

    public UUID getCommentId() {
        return getId();
    }

    public void setCommentId(UUID commentId) {
        setId(commentId);
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
                "commentId=" + getCommentId() +
                ", issueId=" + issueId +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (issueId == null) {
            errors.add("Issue id is mandatory");
        }

        if (userId == null) {
            errors.add("User id is mandatory");
        }

        if (text == null || text.trim().isEmpty()) {
            errors.add("Comment text is mandatory");
        } else if (text.trim().length() > 1000) {
            errors.add("Comment text cannot exceed 1000 characters");
        }
        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getCommentId() != null) {
            errors.add("Comment ID should not be provided for creation");
        }

        if (timestamp != null) {
            errors.add("Created timestamp should not be provided for new comments");
        }
        return errors;
    }

    @Override
    public List<String> validateForUpdate() {
        List<String> errors = validate();

        if (getCommentId() == null) {
            errors.add("Comment ID is required for updates");
        }
        return errors;
    }
}
