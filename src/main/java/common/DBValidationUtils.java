package common;

import java.sql.*;
import java.util.UUID;

public class DBValidationUtils extends AbstractService {

    // ===== USER VALIDATIONS =====

    public boolean userExists(UUID userId) throws Exception {
        if (userId == null) return false;

        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean emailExists(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean emailExistsExcludingUser(String email, UUID excludeUserId) throws Exception {
        if (email == null || email.trim().isEmpty() || excludeUserId == null) return false;

        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND user_id != ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, excludeUserId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== PROJECT VALIDATIONS =====

    public boolean projectExists(UUID projectId) throws Exception {
        if (projectId == null) return false;

        String sql = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean projectNameExists(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM projects WHERE LOWER(name) = LOWER(?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean projectNameExistsExcludingProject(String name, UUID excludeProjectId) throws Exception {
        if (name == null || name.trim().isEmpty() || excludeProjectId == null) return false;

        String sql = "SELECT COUNT(*) FROM projects WHERE LOWER(name) = LOWER(?) AND project_id != ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, excludeProjectId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== ISSUE VALIDATIONS =====

    public boolean issueExists(UUID issueId) throws Exception {
        if (issueId == null) return false;

        String sql = "SELECT COUNT(*) FROM issues WHERE issue_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean issueTitleExistsInProject(String title, UUID projectId) throws Exception {
        if (title == null || title.trim().isEmpty() || projectId == null) return false;

        String sql = "SELECT COUNT(*) FROM issues WHERE LOWER(title) = LOWER(?) AND project_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title.trim());
            ps.setString(2, projectId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean issueTitleDifferentAndExistsInProject(String newTitle, UUID projectId, String currentTitle) throws Exception {
        if (newTitle == null || projectId == null) return false;

        if (currentTitle != null && newTitle.trim().equalsIgnoreCase(currentTitle.trim())) {
            return false;
        }

        return issueTitleExistsInProject(newTitle, projectId);
    }

    // ===== TEAM VALIDATIONS =====

    public boolean teamExists(UUID teamId) throws Exception {
        if (teamId == null) return false;

        String sql = "SELECT COUNT(*) FROM teams WHERE team_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, teamId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean teamNameExists(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM teams WHERE LOWER(name) = LOWER(?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean teamNameExistsExcludingTeam(String name, UUID excludeTeamId) throws Exception {
        if (name == null || name.trim().isEmpty() || excludeTeamId == null) return false;

        String sql = "SELECT COUNT(*) FROM teams WHERE LOWER(name) = LOWER(?) AND team_id != ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, excludeTeamId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean isUserTeamMember(UUID teamId, UUID userId) throws Exception {
        if (teamId == null || userId == null) return false;

        String sql = "SELECT COUNT(*) FROM team_members WHERE team_id = ? AND user_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, teamId.toString());
            ps.setString(2, userId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== LABEL VALIDATIONS =====

    public boolean labelExists(UUID labelId) throws Exception {
        if (labelId == null) return false;

        String sql = "SELECT COUNT(*) FROM issue_labels WHERE label_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, labelId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean labelNameExists(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM issue_labels WHERE LOWER(name) = LOWER(?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean labelNameExistsExcludingLabel(String name, UUID excludeLabelId) throws Exception {
        if (name == null || name.trim().isEmpty() || excludeLabelId == null) return false;

        String sql = "SELECT COUNT(*) FROM issue_labels WHERE LOWER(name) = LOWER(?) AND label_id != ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, excludeLabelId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean isLabelAssignedToIssue(UUID issueId, UUID labelId) throws Exception {
        if (issueId == null || labelId == null) return false;

        String sql = "SELECT COUNT(*) FROM issue_labels_mapping WHERE issue_id = ? AND label_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, issueId.toString());
            ps.setString(2, labelId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== COMMENT VALIDATIONS =====

    public boolean commentExists(UUID commentId) throws Exception {
        if (commentId == null) return false;

        String sql = "SELECT COUNT(*) FROM comments WHERE comment_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, commentId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== ATTACHMENT VALIDATIONS =====

    public boolean attachmentExists(UUID attachmentId) throws Exception {
        if (attachmentId == null) return false;

        String sql = "SELECT COUNT(*) FROM attachments WHERE attachment_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, attachmentId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ===== VALIDATION HELPERS =====

    public void validateUserExists(UUID userId, String fieldName) throws Exception {
        if (!userExists(userId)) {
            throw new IllegalArgumentException(fieldName + " user not found: " + userId);
        }
    }

    public void validateProjectExists(UUID projectId, String fieldName) throws Exception {
        if (!projectExists(projectId)) {
            throw new IllegalArgumentException(fieldName + " project not found: " + projectId);
        }
    }

    public void validateIssueExists(UUID issueId, String fieldName) throws Exception {
        if (!issueExists(issueId)) {
            throw new IllegalArgumentException(fieldName + " issue not found: " + issueId);
        }
    }

    public void validateTeamExists(UUID teamId, String fieldName) throws Exception {
        if (!teamExists(teamId)) {
            throw new IllegalArgumentException(fieldName + " team not found: " + teamId);
        }
    }

    public void validateLabelExists(UUID labelId, String fieldName) throws Exception {
        if (!labelExists(labelId)) {
            throw new IllegalArgumentException(fieldName + " label not found: " + labelId);
        }
    }

    public void validateEmailUnique(String email) throws Exception {
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    public void validateEmailUniqueForUpdate(String email, UUID excludeUserId) throws Exception {
        if (emailExistsExcludingUser(email, excludeUserId)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    public void validateProjectNameUnique(String name) throws Exception {
        if (projectNameExists(name)) {
            throw new IllegalArgumentException("Project name already exists: " + name);
        }
    }

    public void validateProjectNameUniqueForUpdate(String name, UUID excludeProjectId) throws Exception {
        if (projectNameExistsExcludingProject(name, excludeProjectId)) {
            throw new IllegalArgumentException("Project name already exists: " + name);
        }
    }

    public void validateTeamNameUnique(String name) throws Exception {
        if (teamNameExists(name)) {
            throw new IllegalArgumentException("Team name already exists: " + name);
        }
    }

    public void validateTeamNameUniqueForUpdate(String name, UUID excludeTeamId) throws Exception {
        if (teamNameExistsExcludingTeam(name, excludeTeamId)) {
            throw new IllegalArgumentException("Team name already exists: " + name);
        }
    }

    public void validateLabelNameUnique(String name) throws Exception {
        if (labelNameExists(name)) {
            throw new IllegalArgumentException("Label name already exists: " + name);
        }
    }

    public void validateLabelNameUniqueForUpdate(String name, UUID excludeLabelId) throws Exception {
        if (labelNameExistsExcludingLabel(name, excludeLabelId)) {
            throw new IllegalArgumentException("Label name already exists: " + name);
        }
    }

    public void validateIssueTitleUniqueInProject(String title, UUID projectId) throws Exception {
        if (issueTitleExistsInProject(title, projectId)) {
            throw new IllegalArgumentException("Issue title already exists in project: " + title);
        }
    }

    public void validateIssueTitleUniqueInProjectForUpdate(String newTitle, UUID projectId, String currentTitle) throws Exception {
        if (issueTitleDifferentAndExistsInProject(newTitle, projectId, currentTitle)) {
            throw new IllegalArgumentException("Issue title already exists in project: " + newTitle);
        }
    }
    public void validateCommentExists(UUID commentId, String fieldName) throws Exception {
        if (!commentExists(commentId)) {
            throw new IllegalArgumentException(fieldName + " comment not found: " + commentId);
        }
    }

    public void validateAttachmentExists(UUID attachmentId, String fieldName) throws Exception {
        if (!attachmentExists(attachmentId)) {
            throw new IllegalArgumentException(fieldName + " attachment not found: " + attachmentId);
        }
    }

}
