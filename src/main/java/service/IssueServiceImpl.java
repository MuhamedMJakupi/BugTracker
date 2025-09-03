package service;

import common.AbstractService;
import common.DBValidationUtils;
import domain.Issue;
import domain.IssueHistory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class IssueServiceImpl extends AbstractService implements IssueService {

    private final DBValidationUtils validationUtils = new DBValidationUtils();

    private Issue mapIssue(ResultSet rs) throws Exception {
//        Issue issue = new Issue();
//        issue.setIssueId(UUID.fromString(rs.getString("issue_id")));
//        issue.setProjectId(UUID.fromString(rs.getString("project_id")));
//        issue.setTitle(rs.getString("title"));
//        issue.setDescription(rs.getString("description"));
//        issue.setStatusId(rs.getInt("status_id"));
//        issue.setPriorityId(rs.getInt("priority_id"));
//        issue.setReporterId(UUID.fromString(rs.getString("reporter_id")));
//        issue.setAssigneeId(UUID.fromString(rs.getString("assignee_id")));
//        issue.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//        issue.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//        issue.setDueDate(rs.getDate("due_date").toLocalDate());
//        return issue;

        Issue issue = new Issue();
        issue.setIssueId(UUID.fromString(rs.getString("issue_id")));
        issue.setProjectId(UUID.fromString(rs.getString("project_id")));
        issue.setTitle(rs.getString("title"));
        issue.setDescription(rs.getString("description"));
        issue.setStatusId(rs.getInt("status_id"));
        issue.setPriorityId(rs.getInt("priority_id"));
        issue.setReporterId(UUID.fromString(rs.getString("reporter_id")));

        String assigneeIdStr = rs.getString("assignee_id");
        if (assigneeIdStr != null) {
            issue.setAssigneeId(UUID.fromString(assigneeIdStr));
        }

        issue.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().toString());
        issue.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime().toString());

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            issue.setDueDate(dueDate.toString());  // yyyy-MM-dd
        } else {
            issue.setDueDate(null);
        }

        return issue;
    }

    public List<Issue> getAllIssues() throws Exception {
        List<Issue> issues = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_ISSUES)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public Issue getIssueById(UUID issueId) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUE_BY_ID)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapIssue(rs);
            }
            return null;
        }
    }

    public Issue createIssue(Issue issue) throws Exception {
        List<String> errors = issue.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        if(issue.getAssigneeId() != null){
            validationUtils.validateUserExists(issue.getAssigneeId(),"Assignee");
        }
        validationUtils.validateUserExists(issue.getReporterId(), "Reporter");
        validationUtils.validateProjectExists(issue.getProjectId(), "Project");
        validationUtils.validateIssueTitleUniqueInProject(issue.getTitle(), issue.getProjectId());

        issue.setIssueId(UUID.randomUUID());
        issue.setCreatedAt(java.time.LocalDateTime.now().toString());
        issue.setUpdatedAt(java.time.LocalDateTime.now().toString());

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_ISSUE)) {
            ps.setString(1, issue.getIssueId().toString());
            ps.setString(2, issue.getProjectId().toString());
            ps.setString(3, issue.getTitle());
            ps.setString(4, issue.getDescription());
            ps.setInt(5, issue.getStatusId());
            ps.setInt(6, issue.getPriorityId());
            ps.setString(7, issue.getReporterId().toString());
            if (issue.getAssigneeId() != null) {
                ps.setString(8, issue.getAssigneeId().toString());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }
            //ps.setTimestamp(9, Timestamp.valueOf(issue.getCreatedAt()));
            //ps.setTimestamp(10, Timestamp.valueOf(issue.getUpdatedAt()));

            LocalDateTime ldt = LocalDateTime.parse(issue.getCreatedAt());
            ps.setTimestamp(9, Timestamp.valueOf(ldt));

            LocalDateTime ldt2 = LocalDateTime.parse(issue.getUpdatedAt());
            ps.setTimestamp(10, Timestamp.valueOf(ldt2));

            if (issue.getDueDate() != null && !issue.getDueDate().trim().isEmpty()) {
                try {
                    ps.setDate(11, java.sql.Date.valueOf(issue.getDueDate()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid date format for due date: " + issue.getDueDate());
                }
            } else {
                ps.setNull(11, java.sql.Types.DATE);
            }

            ps.executeUpdate();
        }
        return issue;
    }

    @Override
    public void updateIssue(Issue issue, UUID changedByUserId) throws Exception {
        List<String> errors = issue.validateForUpdate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Issue existingIssue = getIssueById(issue.getIssueId());
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found: " + issue.getIssueId());
        }

        if (issue.getAssigneeId() != null) {
            validationUtils.validateUserExists(issue.getAssigneeId(), "Assignee");
        }
        validationUtils.validateUserExists(issue.getReporterId(), "Reporter");
        validationUtils.validateProjectExists(issue.getProjectId(), "Project");
        validationUtils.validateIssueTitleUniqueInProjectForUpdate(
                issue.getTitle(), issue.getProjectId(), existingIssue.getTitle());

        List<IssueHistory> historyEntries = new ArrayList<>();

        if (existingIssue.getStatusId() != issue.getStatusId()) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("status");
            history.setOldValue(String.valueOf(existingIssue.getStatusId()));
            history.setNewValue(String.valueOf(issue.getStatusId()));
            historyEntries.add(history);
        }

        if (!Objects.equals(existingIssue.getAssigneeId(), issue.getAssigneeId())) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("assignee");
            history.setOldValue(existingIssue.getAssigneeId() != null ? existingIssue.getAssigneeId().toString() : null);
            history.setNewValue(issue.getAssigneeId() != null ? issue.getAssigneeId().toString() : null);
            historyEntries.add(history);
        }

        if (existingIssue.getPriorityId() != issue.getPriorityId()) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("priority");
            history.setOldValue(String.valueOf(existingIssue.getPriorityId()));
            history.setNewValue(String.valueOf(issue.getPriorityId()));
            historyEntries.add(history);
        }

        if (!Objects.equals(existingIssue.getTitle(), issue.getTitle())) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("title");
            history.setOldValue(existingIssue.getTitle());
            history.setNewValue(issue.getTitle());
            historyEntries.add(history);
        }

        if (!Objects.equals(existingIssue.getDescription(), issue.getDescription())) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("description");
            history.setOldValue(existingIssue.getDescription());
            history.setNewValue(issue.getDescription());
            historyEntries.add(history);
        }

        if (!Objects.equals(existingIssue.getDueDate(), issue.getDueDate())) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issue.getIssueId());
            history.setChangedByUserId(changedByUserId);
            history.setFieldName("dueDate");
            history.setOldValue(existingIssue.getDueDate());
            history.setNewValue(issue.getDueDate());
            historyEntries.add(history);
        }

        for (IssueHistory history : historyEntries) {
            recordHistoryChange(history);
        }

        issue.setCreatedAt(existingIssue.getCreatedAt());
        issue.setUpdatedAt(LocalDateTime.now().toString());

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.UPDATE_ISSUE)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            ps.setInt(3, issue.getStatusId());
            ps.setInt(4, issue.getPriorityId());
            ps.setString(5, issue.getReporterId().toString());

            if (issue.getAssigneeId() != null) {
                ps.setString(6, issue.getAssigneeId().toString());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }

            //ps.setTimestamp(7, Timestamp.valueOf(issue.getUpdatedAt()));
            LocalDateTime ldt = LocalDateTime.parse(issue.getUpdatedAt());
            ps.setTimestamp(7, Timestamp.valueOf(ldt));

            if (issue.getDueDate() != null && !issue.getDueDate().trim().isEmpty()) {
                try {
                    ps.setDate(8, java.sql.Date.valueOf(issue.getDueDate()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid date format for due date: " + issue.getDueDate());
                }
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }

            ps.setString(9, issue.getIssueId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteIssue(UUID issueId) throws Exception {
        Issue existingIssue = getIssueById(issueId);
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found: " + issueId);
        }
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_ISSUE)) {
            ps.setString(1, issueId.toString());
            ps.executeUpdate();
        }
    }

    public List<Issue> getIssueForProject(UUID projectId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUES_FOR_PROJECT)) {
            ps.setString(1, projectId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> getIssuesByReporter(UUID reporterId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SELECT_ISSUES_BY_REPORTER)) {
            ps.setString(1, reporterId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> getIssuesByAssignee(UUID assigneeId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SELECT_ISSUES_BY_ASSIGNEE)) {
            ps.setString(1, assigneeId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> getIssuesByStatus(int statusId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SELECT_ISSUES_BY_STATUS)) {
            ps.setInt(1, statusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> getIssuesByPriority(int priorityId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SELECT_ISSUES_BY_PRIORITY)) {
            ps.setInt(1, priorityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> searchIssuesByTitle(String searchTerm) throws Exception {
        List<Issue> issues = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SEARCH_ISSUES_BY_TITLE)) {
            ps.setString(1, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    //-----------------------------------FOR ISSUE HISTORY-------------------------------

    public List<IssueHistory> getIssueHistory(UUID issueId) throws Exception {
        List<IssueHistory> historyList = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.GET_ISSUE_HISTORY)) {
            ps.setString(1, issueId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    IssueHistory history = new IssueHistory();
                    history.setHistoryId(UUID.fromString(rs.getString("history_id")));
                    history.setIssueId(UUID.fromString(rs.getString("issue_id")));
                    history.setChangedByUserId(UUID.fromString(rs.getString("changed_by_user_id")));
                    history.setFieldName(rs.getString("field_name"));
                    history.setOldValue(rs.getString("old_value"));
                    history.setNewValue(rs.getString("new_value"));
                    history.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime().toString());
                    historyList.add(history);
                }
            }
        }
        return historyList;
    }
    public void recordHistoryChange(IssueHistory history) throws Exception {
        history.setHistoryId(UUID.randomUUID());
        history.setChangedAt(LocalDateTime.now().toString());

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.RECORD_HISTORY)) {
            ps.setString(1, history.getHistoryId().toString());
            ps.setString(2, history.getIssueId().toString());
            ps.setString(3, history.getChangedByUserId().toString());
            ps.setString(4, history.getFieldName());
            ps.setString(5, history.getOldValue());
            ps.setString(6, history.getNewValue());
            //ps.setTimestamp(7, Timestamp.valueOf(history.getChangedAt()));
            LocalDateTime ldt = LocalDateTime.parse(history.getChangedAt());
            ps.setTimestamp(7, Timestamp.valueOf(ldt));
            ps.executeUpdate();
        }
    }
    public static class SQL {
        public static final String ALL_ISSUES = "SELECT * FROM issues";
        public static final String ISSUE_BY_ID = "SELECT * FROM issues WHERE issue_id = ?";

        public static final String CREATE_ISSUE = """
                INSERT INTO issues (issue_id, project_id, title, description, status_id, priority_id, reporter_id, assignee_id, created_at, updated_at, due_date) 
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """;
        public static final String UPDATE_ISSUE = """
                UPDATE issues 
                SET title=?, description=?, status_id=?, priority_id=?, reporter_id=?, assignee_id=?, updated_at=?, due_date=? 
                WHERE issue_id=?
                """;
        public static final String DELETE_ISSUE = "DELETE FROM issues WHERE issue_id = ?";

        public static final String ISSUES_FOR_PROJECT = "SELECT * FROM issues WHERE project_id = ?";
        public static final String SELECT_ISSUES_BY_REPORTER =
                "SELECT * FROM issues WHERE reporter_id = ? ORDER BY created_at DESC";
        public static final String SELECT_ISSUES_BY_ASSIGNEE =
                "SELECT * FROM issues WHERE assignee_id = ? ORDER BY created_at DESC";
        public static final String SELECT_ISSUES_BY_STATUS =
                "SELECT * FROM issues WHERE status_id = ? ORDER BY created_at DESC";
        public static final String SELECT_ISSUES_BY_PRIORITY =
                "SELECT * FROM issues WHERE priority_id = ? ORDER BY created_at DESC";
        public static final String SEARCH_ISSUES_BY_TITLE =
                "SELECT * FROM issues WHERE title LIKE ? ORDER BY created_at DESC";

        public static final String GET_ISSUE_HISTORY = """
                        SELECT h.*, u.name as changed_by_name
                        FROM issue_history h
                        JOIN users u ON h.changed_by_user_id = u.user_id
                        WHERE h.issue_id = ?
                        ORDER BY h.changed_at DESC
                """;
        public static final String RECORD_HISTORY  = """
                INSERT INTO issue_history (history_id, issue_id, changed_by_user_id, field_name, old_value, new_value, changed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;


    }

    //---------- Down are 2 different methods of update, separately with/without history track --------------------


//    public void updateIssue(Issue issue) throws Exception {
//
//        List<String> errors = issue.validateForUpdate();
//        if (!errors.isEmpty()) {
//            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
//        }
//
//        Issue existingIssue = getIssueById(issue.getIssueId());
//
//        if (existingIssue == null) {
//            throw new IllegalArgumentException("Issue not found: " + issue.getIssueId());
//        }
//        if (issue.getAssigneeId() != null && !userExists(issue.getAssigneeId())) {
//            throw new IllegalArgumentException("User not found: " + issue.getAssigneeId());
//        }
//        if (!userExists(issue.getReporterId())) {
//            throw new IllegalArgumentException("User not found: " + issue.getReporterId());
//        }
//        if(!projectExists(issue.getProjectId())) {
//            throw new IllegalArgumentException("Project not found: " + issue.getProjectId());
//        }
//        if(issueTitleExistsInProject(issue.getTitle(), issue.getProjectId()) ) {
//            throw new IllegalArgumentException("Title already exists: " + issue.getTitle());
//        }
//
//
//        issue.setCreatedAt(existingIssue.getCreatedAt());
//        issue.setUpdatedAt(java.time.LocalDateTime.now());
//
//        //String sql = "UPDATE issue SET title=?, description=?, status_id=?, priority_id=?, reported_id=?, assignee_id=?, updated_at=?, due_date=? WHERE issue_id=?";
//
//        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_ISSUE)) {
//            ps.setString(1, issue.getTitle());
//            ps.setString(2, issue.getDescription());
//            ps.setInt(3, issue.getStatusId());
//            ps.setInt(4, issue.getPriorityId());
//            ps.setString(5, issue.getReporterId().toString());
//            //ps.setString(6, issue.getAssigneeId().toString());
//            if (issue.getAssigneeId() != null) {
//                ps.setString(6, issue.getAssigneeId().toString());
//            } else {
//                ps.setNull(6, java.sql.Types.VARCHAR);
//            }
//            ps.setTimestamp(7, Timestamp.valueOf(issue.getUpdatedAt()));
//            //ps.setDate(8, Date.valueOf(issue.getDueDate()));
//            if (issue.getDueDate() != null) {
//                ps.setDate(8, java.sql.Date.valueOf(issue.getDueDate()));
//            } else {
//                ps.setNull(8, java.sql.Types.DATE);
//            }
//            ps.setString(9, issue.getIssueId().toString());
//            ps.executeUpdate();
//        }
//    }


//    public void updateIssueWithHistory(Issue issue, UUID changedByUserId) throws Exception {
//        List<String> errors = issue.validateForUpdate();
//        if (!errors.isEmpty()) {
//            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
//        }
//
//        Issue existingIssue = getIssueById(issue.getIssueId());
//        if (existingIssue == null) {
//            throw new IllegalArgumentException("Issue not found: " + issue.getIssueId());
//        }
//
//        List<IssueHistory> historyEntries = new ArrayList<>();
//
//        if (existingIssue.getStatusId() != issue.getStatusId()) {
//            IssueHistory history = new IssueHistory();
//            history.setIssueId(issue.getIssueId());
//            history.setChangedByUserId(changedByUserId);
//            history.setFieldName("status");
//            history.setOldValue(String.valueOf(existingIssue.getStatusId()));
//            history.setNewValue(String.valueOf(issue.getStatusId()));
//            historyEntries.add(history);
//        }
//
//        if (!Objects.equals(existingIssue.getAssigneeId(), issue.getAssigneeId())) {
//            IssueHistory history = new IssueHistory();
//            history.setIssueId(issue.getIssueId());
//            history.setChangedByUserId(changedByUserId);
//            history.setFieldName("assignee");
//            history.setOldValue(existingIssue.getAssigneeId() != null ? existingIssue.getAssigneeId().toString() : null);
//            history.setNewValue(issue.getAssigneeId() != null ? issue.getAssigneeId().toString() : null);
//            historyEntries.add(history);
//        }
//
//        if (existingIssue.getPriorityId() != issue.getPriorityId()) {
//            IssueHistory history = new IssueHistory();
//            history.setIssueId(issue.getIssueId());
//            history.setChangedByUserId(changedByUserId);
//            history.setFieldName("priority");
//            history.setOldValue(String.valueOf(existingIssue.getPriorityId()));
//            history.setNewValue(String.valueOf(issue.getPriorityId()));
//            historyEntries.add(history);
//        }
//
//        for (IssueHistory history : historyEntries) {
//            recordHistoryChange(history);
//        }
//
//        updateIssue(issue);
//    }
}



