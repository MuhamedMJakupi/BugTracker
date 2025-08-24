package service;

import common.AbstractService;
import domain.Attachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttachmentService extends AbstractService {
    private boolean issueExists(UUID issueId) throws Exception {
        //String sql = "SELECT COUNT(*) FROM issue WHERE issue_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ISSUE_EXISTS)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Attachment mapAttachment(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setAttachmentId(UUID.fromString(rs.getString("attachment_id")));
        attachment.setIssueId(UUID.fromString(rs.getString("issue_id")));
        attachment.setFilename(rs.getString("filename"));
        attachment.setFileUrl(rs.getString("file_url"));
        attachment.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        return attachment;
    }

    private final AttachmentValidationService attachmentValidationService = new AttachmentValidationService();

    public Attachment getAttachmentById(UUID attachmentId) throws Exception {
        //String sql = "SELECT * FROM attachments WHERE attachment_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ATTACHMENT_BY_ID)) {
            ps.setString(1, attachmentId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAttachment(rs);
            }
            return null;
        }
    }

    public List<Attachment> getAllAttachments() throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        //String sql = "SELECT * FROM attachments";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_ATTACHMENTS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                attachments.add(mapAttachment(rs));
            }
        }
        return attachments;
    }

    public Attachment createAttachment(Attachment attachment)  throws Exception {
        List<String> errors = attachmentValidationService.validateAttachmentForCreation(attachment);
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed:  " + String.join(", ", errors));
        }
        if (!issueExists(attachment.getIssueId())) {
            throw new IllegalArgumentException("Issue not found:  " + String.join(", ", errors));
        }

        attachment.setAttachmentId(UUID.randomUUID());
        attachment.setUploadedAt(java.time.LocalDateTime.now());

        //String sql = "INSERT INTO attachments (attachment_id, issue_id, filename, file_url, uploaded ) VALUES (?,?,?,?,?)";

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_ATTACHMENT)) {
            ps.setString(1, attachment.getAttachmentId().toString());
            ps.setString(2, attachment.getIssueId().toString());
            ps.setString(3, attachment.getFilename());
            ps.setString(4, attachment.getFileUrl());
            ps.setTimestamp(5, Timestamp.valueOf(attachment.getUploadedAt()));
            ps.executeUpdate();
        }
        return  attachment;
    }

    public void updateAttachment(Attachment attachment)  throws Exception {
        List<String> errors = attachmentValidationService.validateAttachmentForUpdate(attachment);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        Attachment existingAttachment = getAttachmentById(attachment.getAttachmentId());

        if(existingAttachment == null) {
            throw new IllegalArgumentException("Attachment not found: " + attachment.getAttachmentId());
        }
        if (!issueExists(attachment.getIssueId())) {
            throw new IllegalArgumentException("Issue not found: " + attachment.getIssueId());
        }

        attachment.setUploadedAt(java.time.LocalDateTime.now());

        //String sql = "UPDATE attachments SET filename=?, file_url=?, uploaded_at = ? where attachment_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_ATTACHMENT)) {
            ps.setString(1, attachment.getFilename());
            ps.setString(2, attachment.getFileUrl());
            ps.setString(3, attachment.getUploadedAt().toString());
            ps.setString(4, attachment.getAttachmentId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteAttachment(UUID attachmentID)  throws Exception {
        Attachment existingAttachment = getAttachmentById(attachmentID);
        if(existingAttachment == null) {
            throw new IllegalArgumentException("Attachment not found: " + attachmentID);
        }
        //String sql = "DELETE FROM attachments WHERE attachment_id = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_ATTACHMENT)) {
            ps.setString(1, attachmentID.toString());
            ps.executeUpdate();
        }
    }

    public List<Attachment> getAttachmentsByIssueId(UUID issueId) throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        //String sql = "SELECT * FROM ATTACHMENT WHERE ISSUE_ID = ?";
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ATTACHMENT_BY_ISSUE_ID)) {
            ps.setString(1, issueId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                attachments.add(mapAttachment(rs));
            }
        }
        return attachments;
    }

    public static class SQL {
        public static final String ISSUE_EXISTS = "SELECT COUNT(*) FROM issues WHERE issue_id = ?";
        public static final String ATTACHMENT_BY_ID = "SELECT * FROM attachments WHERE attachment_id = ?";
        public static final String ALL_ATTACHMENTS = "SELECT * FROM attachments";
        public static final String CREATE_ATTACHMENT = " INSERT INTO attachments (attachment_id, issue_id, filename, file_url, uploaded_at ) VALUES (?,?,?,?,?)";
        public static final String UPDATE_ATTACHMENT = " UPDATE attachments SET filename=?, file_url=?, uploaded_at = ? where attachment_id = ?";

        public static final String DELETE_ATTACHMENT = " DELETE FROM attachments WHERE attachment_id = ?";

        public static final String ATTACHMENT_BY_ISSUE_ID ="SELECT * FROM attachments WHERE issue_id = ?";
    }
}
