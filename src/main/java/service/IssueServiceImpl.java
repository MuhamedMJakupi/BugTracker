package service;

import common.AbstractService;
import domain.Issue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IssueServiceImpl extends AbstractService implements IssueService {
    private boolean userExists(UUID userId) throws Exception {
        //String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.USER_EXISTS)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    private boolean projectExists(UUID projectId) throws Exception {
        //String sql = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.PROJECT_EXISTS)) {
            ps.setString(1, projectId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        }
    }
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

        issue.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        issue.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            issue.setDueDate(dueDate.toLocalDate());
        }

        return issue;
    }

    private boolean issueTitleExistsInProject(String title, UUID projectId) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.CHECK_ISSUE_TITLE_EXISTS_IN_PROJECT)) {
            ps.setString(1, title);
            ps.setString(2, projectId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Issue createIssue(Issue issue) throws Exception {
        List<String> errors = issue.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if (issue.getAssigneeId() != null && !userExists(issue.getAssigneeId())) {
            throw new IllegalArgumentException("User not found: " + issue.getAssigneeId());
        }
        if (!userExists(issue.getReporterId())) {
            throw new IllegalArgumentException("User not found: " + issue.getReporterId());
        }
        if(!projectExists(issue.getProjectId())) {
            throw new IllegalArgumentException("Project not found: " + issue.getProjectId());
        }
        if(issueTitleExistsInProject(issue.getTitle(), issue.getProjectId()) ) {
            throw new IllegalArgumentException("Title already exists: " + issue.getTitle());
        }

        issue.setIssueId(UUID.randomUUID());
        issue.setCreatedAt(java.time.LocalDateTime.now());
        issue.setUpdatedAt(java.time.LocalDateTime.now());

        //String sql = "INSERT INTO issues (issue_id, project_id, title, description, status_id, priority_id, reporter_id, assignee_id, created_at, updated_at, due_date) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_ISSUE)) {
            ps.setString(1, issue.getIssueId().toString());
            ps.setString(2, issue.getProjectId().toString());
            ps.setString(3, issue.getTitle());
            ps.setString(4, issue.getDescription());
            ps.setInt(5, issue.getStatusId());
            ps.setInt(6, issue.getPriorityId());
            ps.setString(7, issue.getReporterId().toString());
            //ps.setString(8, issue.getAssigneeId().toString());
            if (issue.getAssigneeId() != null) {
                ps.setString(8, issue.getAssigneeId().toString());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }
            ps.setTimestamp(9, Timestamp.valueOf(issue.getCreatedAt()));
            ps.setTimestamp(10, Timestamp.valueOf(issue.getUpdatedAt()));
            //ps.setDate(11,Date.valueOf(issue.getDueDate()));
            if (issue.getDueDate() != null) {
                ps.setDate(11, java.sql.Date.valueOf(issue.getDueDate()));
            } else {
                ps.setNull(11, java.sql.Types.DATE);
            }
            ps.executeUpdate();
        }
        return issue;
    }

    public void updateIssue(Issue issue) throws Exception {

        List<String> errors = issue.validateForUpdate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Issue existingIssue = getIssueById(issue.getIssueId());

        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found: " + issue.getIssueId());
        }
        if (issue.getAssigneeId() != null && !userExists(issue.getAssigneeId())) {
            throw new IllegalArgumentException("User not found: " + issue.getAssigneeId());
        }
        if (!userExists(issue.getReporterId())) {
            throw new IllegalArgumentException("User not found: " + issue.getReporterId());
        }
        if(!projectExists(issue.getProjectId())) {
            throw new IllegalArgumentException("Project not found: " + issue.getProjectId());
        }
        if(issueTitleExistsInProject(issue.getTitle(), issue.getProjectId()) ) {
            throw new IllegalArgumentException("Title already exists: " + issue.getTitle());
        }


        issue.setCreatedAt(existingIssue.getCreatedAt());
        issue.setUpdatedAt(java.time.LocalDateTime.now());

        //String sql = "UPDATE issue SET title=?, description=?, status_id=?, priority_id=?, reported_id=?, assignee_id=?, updated_at=?, due_date=? WHERE issue_id=?";

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_ISSUE)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            ps.setInt(3, issue.getStatusId());
            ps.setInt(4, issue.getPriorityId());
            ps.setString(5, issue.getReporterId().toString());
            //ps.setString(6, issue.getAssigneeId().toString());
            if (issue.getAssigneeId() != null) {
                ps.setString(6, issue.getAssigneeId().toString());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }
            ps.setTimestamp(7, Timestamp.valueOf(issue.getUpdatedAt()));
            //ps.setDate(8, Date.valueOf(issue.getDueDate()));
            if (issue.getDueDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(issue.getDueDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setString(9, issue.getIssueId().toString());
            ps.executeUpdate();
        }
    }

    public Issue getIssueById(UUID issueId) throws Exception {
        //String sql = "SELECT * FROM issues WHERE issue_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUE_BY_ID)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapIssue(rs);
            }
            return null;
        }
    }

    public List<Issue> getAllIssues() throws Exception {
        List<Issue> issues = new ArrayList<>();
        //String sql = "SELECT * FROM issues";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_ISSUES)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public List<Issue> getIssueForProject(UUID projectId) throws Exception {
        List<Issue> issues = new ArrayList<>();
        //String sql = "SELECT * FROM issue WHERE project_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUES_FOR_PROJECT)) {
            ps.setString(1, projectId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issues.add(mapIssue(rs));
            }
        }
        return issues;
    }

    public void deleteIssue(UUID issueId) throws Exception {
        Issue existingIssue = getIssueById(issueId);
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found: " + issueId);
        }
        //String sql = "DELETE FROM issues WHERE issue_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_ISSUE)) {
            ps.setString(1, issueId.toString());
            ps.executeUpdate();
        }
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

    public static class SQL {
        public static final String USER_EXISTS = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        public static final String PROJECT_EXISTS = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        public static final String CREATE_ISSUE = """
                INSERT INTO issues (issue_id, project_id, title, description, status_id, priority_id, reporter_id, assignee_id, created_at, updated_at, due_date) 
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """;
        public static final String ISSUE_BY_ID = "SELECT * FROM issues WHERE issue_id = ?";
        public static final String UPDATE_ISSUE = """
                UPDATE issues 
                SET title=?, description=?, status_id=?, priority_id=?, reported_id=?, assignee_id=?, updated_at=?, due_date=? 
                WHERE issue_id=?
                """;
        public static final String ALL_ISSUES = "SELECT * FROM issues";
        public static final String ISSUES_FOR_PROJECT = "SELECT * FROM issues WHERE project_id = ?";
        public static final String DELETE_ISSUE = "DELETE FROM issues WHERE issue_id = ?";
        public static final String CHECK_ISSUE_TITLE_EXISTS_IN_PROJECT =
                "SELECT COUNT(*) FROM issues WHERE title = ? AND project_id = ?";
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
    }
}



