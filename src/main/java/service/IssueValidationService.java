package service;

import common.enums.IssuePriority;
import common.enums.IssueStatus;
import domain.Issue;
import domain.Project;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueValidationService {

    public List<String> validateIssues(Issue issue) {
        List<String> errors = new ArrayList<>();

        if (issue.getTitle() == null || issue.getTitle().trim().isEmpty()) {
            errors.add("Issue title is mandatory");
        } else if (issue.getTitle().length() < 3 || issue.getTitle().length() > 200) {
            errors.add("Issue title must be between 3 and 200 characters");
        }

        if (issue.getDescription() != null && issue.getDescription().length() > 1000) {
            errors.add("Issue description cannot exceed 1000 characters");
        }
        if (issue.getProjectId() == null) {
            errors.add("Project ID is mandatory");
        }
        if (issue.getReporterId() == null) {
            errors.add("Reporter ID is mandatory");
        }

        if (!isValidStatus(issue.getStatusId())) {
            errors.add("Invalid issue status");
        }

        if (!isValidPriority(issue.getPriorityId())) {
            errors.add("Invalid issue priority");
        }
        if (issue.getDueDate() != null && issue.getDueDate().isBefore(LocalDate.now())) {
            errors.add("Due date cannot be in the past");
        }
        return errors;
    }
    public List<String> validateIssueForUpdate(Issue issue) {
        List<String> errors = validateIssues (issue);
        if(issue.getIssueId() == null) {
            errors.add("Issue ID is required for updates");
        }
        return errors;
    }
    public List<String> validateIssueForCreate(Issue issue) {
        List<String> errors = validateIssues (issue);

        if (issue.getIssueId() != null) {
            errors.add("Issue ID should not be provided for new issues");
        }

        if (issue.getCreatedAt() != null) {
            errors.add("Created timestamp should not be provided for new issues");
        }

        if (issue.getUpdatedAt() != null) {
            errors.add("Updated timestamp should not be provided for new issues");
        }
        return errors;
    }

    public boolean isValidStatus(int statusId) {
        try {
            IssueStatus.fromId(statusId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isValidPriority(int priorityId) {
        try {
            IssuePriority.fromId(priorityId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
