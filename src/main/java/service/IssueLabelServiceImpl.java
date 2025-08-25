package service;

import common.AbstractService;
import domain.IssueLabel;
import domain.IssueLabelMapping;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IssueLabelServiceImpl extends AbstractService implements  IssueLabelService {

    private boolean labelNameExists(String name) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.LABEL_EXISTS)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean labelNameExistsExcludingLabel(String name, UUID excludeLabelId) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.LABEL_NAME_EXISTS_EXCLUDING)) {
            ps.setString(1, name.toLowerCase());
            ps.setString(2, excludeLabelId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean isLabelAssigned(UUID issueId, UUID labelId) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.CHECK_LABEL_ASSIGNMENT)) {
            ps.setString(1, issueId.toString());
            ps.setString(2, labelId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public IssueLabel createLabel(IssueLabel label) throws Exception {
        List<String> errors = label.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        if (labelNameExists(label.getName()))
            throw new IllegalArgumentException("Label already exists.");

        label.setLabelId(UUID.randomUUID());
        label.setCreatedAt(java.time.LocalDateTime.now());

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.CREATE_LABEL)) {
            ps.setString(1, label.getLabelId().toString());
            ps.setString(2, label.getName());
            ps.setTimestamp(3, Timestamp.valueOf(label.getCreatedAt()));
            ps.executeUpdate();
        }
        return label;
    }

    public void updateLabel(IssueLabel label) throws Exception {
        List<String> errors = label.validateForUpdate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        IssueLabel existingLabel = getLabelById(label.getLabelId());
        if (existingLabel == null) {
            throw new IllegalArgumentException("Label not found: " + label.getLabelId());
        }

        if (labelNameExistsExcludingLabel(label.getName(), label.getLabelId())) {
            throw new IllegalArgumentException("Label name already exists: " + label.getName());
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.UPDATE_LABEL)) {
            ps.setString(1, label.getName());
            ps.setString(2, label.getLabelId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteLabel(UUID labelId) throws Exception {
        IssueLabel existingLabel = getLabelById(labelId);
        if (existingLabel == null) {
            throw new IllegalArgumentException("Label not found: " + labelId);
        }
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.DELETE_LABEL)) {
            ps.setString(1, labelId.toString());
            ps.executeUpdate();
        }
    }

    public List<IssueLabel> getAllLabels() throws Exception {
        List<IssueLabel> labels = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SELECT_ALL_LABELS)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labels.add(mapLabel(rs));
                }
            }
        }
        return labels;
    }
    public IssueLabel getLabelById(UUID labelId) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.LABEL_BY_ID)) {
            ps.setString(1, labelId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLabel(rs);
                }
                return null;
            }
        }
    }

    public void addLabelToIssue(UUID issueId, UUID labelId) throws Exception {

        IssueLabelMapping mapping = new IssueLabelMapping();
        mapping.setIssueId(issueId);
        mapping.setLabelId(labelId);

        List<String> errors = mapping.validateForCreation();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        if (isLabelAssigned(issueId, labelId))
            throw new IllegalArgumentException("Label already assigned to issue.");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.ADD_LABEL_TO_ISSUE)) {
            ps.setString(1, issueId.toString());
            ps.setString(2, labelId.toString());
            ps.executeUpdate();
        }
    }

    public void removeLabelFromIssue(UUID issueId, UUID labelId) throws Exception {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.REMOVE_LABEL_FROM_ISSUE)) {
            ps.setString(1, issueId.toString());
            ps.setString(2, labelId.toString());
            ps.executeUpdate();
        }
    }

    public List<IssueLabel> getLabelsForIssue(UUID issueId) throws Exception {
        List<IssueLabel> labels = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.LABELS_FOR_ISSUE)) {
            ps.setString(1, issueId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labels.add(mapLabel(rs));
                }
            }
        }
        return labels;
    }

    public List<IssueLabel> searchLabelsByName(String query) throws Exception {
        List<IssueLabel> labels = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL.SEARCH_LABELS)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labels.add(mapLabel(rs));
                }
            }
        }
        return labels;
    }

    private IssueLabel mapLabel(ResultSet rs) throws SQLException {
        IssueLabel label = new IssueLabel();
        label.setLabelId(UUID.fromString(rs.getString("label_id")));
        label.setName(rs.getString("name"));
        label.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return label;
    }

    public static class SQL {
        public static final String CREATE_LABEL =
                "INSERT INTO issue_labels (label_id, name, created_at) VALUES (?, ?, ?)";
        public static final String UPDATE_LABEL = "UPDATE issue_labels SET name = ? WHERE label_id = ?";
        public static final String DELETE_LABEL = "DELETE FROM issue_labels WHERE label_id = ?";
        public static final String SELECT_ALL_LABELS = "SELECT * FROM issue_labels ORDER BY name";
        public static final String LABEL_EXISTS = "SELECT COUNT(*) FROM issue_labels WHERE LOWER(name) = LOWER(?)";
        public static final String ADD_LABEL_TO_ISSUE =
                "INSERT INTO issue_labels_mapping (issue_id, label_id) VALUES (?, ?)";
        public static final String REMOVE_LABEL_FROM_ISSUE =
                "DELETE FROM issue_labels_mapping WHERE issue_id = ? AND label_id = ?";
        public static final String LABELS_FOR_ISSUE = """
                    SELECT l.* 
                    FROM issue_labels l 
                    INNER JOIN issue_labels_mapping ill ON l.label_id = ill.label_id    
                    WHERE ill.issue_id = ?
                    """;
        public static final String SEARCH_LABELS = "SELECT * FROM issue_labels WHERE LOWER(name) LIKE LOWER(?)";
        public static final String CHECK_LABEL_ASSIGNMENT =
                "SELECT COUNT(*) FROM issue_labels_mapping WHERE issue_id = ? AND label_id = ?";
        public static final String LABEL_BY_ID = "SELECT * FROM issue_labels WHERE label_id = ?";
        public static final String LABEL_NAME_EXISTS_EXCLUDING =
                "SELECT COUNT(*) FROM issue_labels WHERE LOWER(name) = LOWER(?) AND label_id != ?";
    }
}
