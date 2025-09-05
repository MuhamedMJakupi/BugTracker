package domain;

import common.AbstractEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IssueHistory extends AbstractEntity {

    private UUID issueId;
    private UUID changedByUserId;
    private String fieldName;        // status, assignee, priority...
    private String oldValue;
    private String newValue;
    private String changedAt;

    public UUID getHistoryId() {
        return getId();
    }

    public void setHistoryId(UUID historyId) {
        setId(historyId);
    }

    public UUID getIssueId() { return issueId; }
    public void setIssueId(UUID issueId) { this.issueId = issueId; }

    public UUID getChangedByUserId() { return changedByUserId; }
    public void setChangedByUserId(UUID changedByUserId) { this.changedByUserId = changedByUserId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getChangedAt() { return changedAt; }
    public void setChangedAt(String changedAt) { this.changedAt = changedAt; }

    private static final Set<String> ALLOWED_FIELDS =
            Set.of("statusId", "assigneeId", "priorityId", "title", "description","duedate");

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (issueId == null) {
            errors.add("Issue ID is required");
        }

        if (changedByUserId == null) {
            errors.add("Changed by user ID is required");
        }

        if (fieldName == null || fieldName.trim().isEmpty()) {
            errors.add("Field name is required");
        } else if (!ALLOWED_FIELDS.contains(fieldName.toLowerCase().trim())) {
            errors.add("Invalid field name: " + fieldName);
        }

        if (oldValue != null && oldValue.length() > 1000) {
            errors.add("Old value cannot exceed 1000 characters");
        }

        if (newValue != null && newValue.length() > 1000) {
            errors.add("New value cannot exceed 1000 characters");
        }

        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getHistoryId() != null) {
            errors.add("History ID should not be provided for new history entries");
        }

        if (changedAt != null) {
            errors.add("Changed timestamp should not be provided for new history entries");
        }
        return errors;
    }

}
