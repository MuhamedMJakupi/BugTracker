package domain;

import common.enums.IssueStatus;
import common.enums.IssuePriority;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

public class Issue {

    private UUID issueId;
    private UUID projectId;
    private String title;
    private String description;
    private int statusId;
    private int priorityId;
    private UUID reporterId;
    private UUID assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate dueDate;

    public Issue() {}

    // Minimal constructor
    public Issue(UUID projectId, String title, String description, UUID reporterId) {
        this.issueId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.statusId = IssueStatus.TODO.getId();        // Default status
        this.priorityId = IssuePriority.MEDIUM.getId();  // Default priority

        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.reporterId = reporterId;

        // assigneeId and dueDate remain null (to be set later)
    }

    // Full constructor when all details are known
    public Issue(UUID projectId, String title, String description, UUID reporterId,
                 UUID assigneeId, int statusId, int priorityId, LocalDate dueDate) {
        this.issueId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.reporterId = reporterId;
        this.assigneeId = assigneeId;
        this.statusId = statusId;
        this.priorityId = priorityId;
        this.dueDate = dueDate;
    }

    public UUID getIssueId() { return issueId; }
    public void setIssueId(UUID issueId) { this.issueId = issueId; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) {
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

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueId=" + issueId +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", statusId=" + statusId +
                ", priorityId=" + priorityId +
                ", reporterId=" + reporterId +
                ", assigneeId=" + assigneeId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dueDate=" + dueDate +
                '}';
    }
}
