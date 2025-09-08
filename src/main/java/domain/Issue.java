package domain;

import common.AbstractEntity;
import common.enums.IssueStatus;
import common.enums.IssuePriority;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Issue extends AbstractEntity {

    private UUID projectId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private int statusId;
    private int priorityId;
    private UUID reporterId;
    private UUID assigneeId;
    private String createdAt;
    private String updatedAt;
    private String dueDate;

    public Issue() {}

    public Issue(UUID projectId, String title, String description, UUID reporterId) {

        setIssueId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now().toString();
        this.updatedAt = LocalDateTime.now().toString();
        this.statusId = IssueStatus.TODO.getId();        // Default status
        this.priorityId = IssuePriority.MEDIUM.getId();  // Default priority

        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.reporterId = reporterId;

        // assigneeId and dueDate remain null (to be set later)
    }

    public Issue(UUID projectId, String title, String description, UUID reporterId,
                 UUID assigneeId, int statusId, int priorityId, String dueDate) {

        setIssueId(UUID.randomUUID());
        this.createdAt = LocalDateTime.now().toString();
        this.updatedAt = LocalDateTime.now().toString();

        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.reporterId = reporterId;
        this.assigneeId = assigneeId;
        this.statusId = statusId;
        this.priorityId = priorityId;
        this.dueDate = dueDate;
    }

    public UUID getIssueId() { return getId() ; }
    public void setIssueId(UUID issueId) { setId(issueId); }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
        updateTimestamp();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        updateTimestamp();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) {
        this.statusId = statusId;
        updateTimestamp();
    }

    public int getPriorityId() { return priorityId; }
    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
        updateTimestamp();
    }

    public UUID getReporterId() { return reporterId; }
    public void setReporterId(UUID reporterId) { this.reporterId = reporterId; }

    public UUID getAssigneeId() { return assigneeId; }
    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
        updateTimestamp();
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
        updateTimestamp();
    }

    public IssueStatus getStatus() {
        return IssueStatus.fromId(this.statusId);
    }

    public void setStatus(IssueStatus status) {
        this.statusId = status.getId();
        updateTimestamp();
    }

    public IssuePriority getPriority() {
        return IssuePriority.fromId(this.priorityId);
    }

    public void setPriority(IssuePriority priority) {
        this.priorityId = priority.getId();
        updateTimestamp();
    }

    public String getStatusString() {
        return status;
    }
    public void setStatusString(String status) {
        this.status = status;
    }
    public String getPriorityString() {
        return priority;
    }
    public void setPriorityString(String priority) {
        this.priority = priority;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now().toString();
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueId=" + getIssueId() +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", statusId=" + statusId +
                ", priorityId=" + priorityId +
                ", reporterId=" + reporterId +
                ", assigneeId=" + assigneeId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            errors.add("Issue title is mandatory");
        } else if (title.trim().length() < 3 || title.trim().length() > 200) {
            errors.add("Issue title must be between 3 and 200 characters");
        }

        if (description != null && description.trim().length() > 1000) {
            errors.add("Issue description cannot exceed 1000 characters");
        }

        if (projectId == null || projectId.toString().trim().isEmpty()) {
            errors.add("Project ID is mandatory");
        }

        if (reporterId == null || reporterId.toString().trim().isEmpty()) {
            errors.add("Reporter ID is mandatory");
        }

        boolean hasPriorityString = priority != null && !priority.trim().isEmpty();
        boolean hasPriorityId = priorityId > 0;

        if (hasPriorityString == hasPriorityId) {
            errors.add("Exactly one of 'priority' or 'priorityId' must be provided, not both");
        } else {
            if (hasPriorityString) {
                try {
                    IssuePriority.fromName(priority.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid priority: " + priority);
                }
            } else {
                if (priorityId < 1 || priorityId > 4) {
                    errors.add("Invalid priorityId: must be between 1 and 4");
                }
            }
        }

        boolean hasStatusString = status != null && !status.trim().isEmpty();
        boolean hasStatusId = statusId > 0;

        if (hasStatusString == hasStatusId) {
            errors.add("Exactly one of 'status' or 'statusId' must be provided, not both");
        } else {
            if (hasStatusString) {
                try {
                    IssueStatus.fromName(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid status: " + status);
                }
            } else {
                if (statusId < 1 || statusId > 4) {
                    errors.add("Invalid statusId: must be between 1 and 4");
                }
            }
        }

        if (dueDate != null && !dueDate.trim().isEmpty()) {
            if (!isValidDateString(dueDate)) {
                errors.add("Due date must be in yyyy-MM-dd format");
            } else {
                LocalDate dueDateParsed = LocalDate.parse(dueDate);
                if (dueDateParsed.isBefore(LocalDate.now())) {
                    errors.add("Due date cannot be in the past");
                }
            }
        }

        return errors;    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getIssueId() != null) {
            errors.add("Issue ID should not be provided for new issues");
        }

        if (createdAt != null) {
            errors.add("Created timestamp should not be provided for new issues");
        }

        if (updatedAt != null) {
            errors.add("Updated timestamp should not be provided for new issues");
        }

        return errors;
    }

    private boolean isValidDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
