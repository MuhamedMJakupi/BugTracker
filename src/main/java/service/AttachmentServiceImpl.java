package service;

import common.AbstractService;
import common.DBValidationUtils;
import domain.Attachment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttachmentServiceImpl extends AbstractService implements  AttachmentService {

    private final DBValidationUtils validationUtils = new DBValidationUtils();

    private Attachment mapAttachment(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setAttachmentId(UUID.fromString(rs.getString("attachment_id")));
        attachment.setIssueId(UUID.fromString(rs.getString("issue_id")));
        attachment.setFilename(rs.getString("filename"));
        attachment.setFileUrl(rs.getString("file_url"));
        attachment.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime().toString());
        return attachment;
    }

    public List<Attachment> getAllAttachments() throws Exception {
        List<Attachment> attachments = new ArrayList<>();
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ALL_ATTACHMENTS)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                attachments.add(mapAttachment(rs));
            }
        }
        return attachments;
    }

    public Attachment getAttachmentById(UUID attachmentId) throws Exception {
        validationUtils.validateAttachmentExists(attachmentId,"Attachment");
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.ATTACHMENT_BY_ID)) {
            ps.setString(1, attachmentId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAttachment(rs);
            }
            return null;
        }
    }

    public Attachment createAttachment(Attachment attachment)  throws Exception {
        List<String> errors = attachment.validateForCreation();
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed:  " + String.join(", ", errors));
        }

        validationUtils.validateIssueExists(attachment.getIssueId(),"Issue");
        attachment.setAttachmentId(UUID.randomUUID());
        attachment.setUploadedAt(LocalDateTime.now().toString());

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.CREATE_ATTACHMENT)) {
            ps.setString(1, attachment.getAttachmentId().toString());
            ps.setString(2, attachment.getIssueId().toString());
            ps.setString(3, attachment.getFilename());
            ps.setString(4, attachment.getFileUrl());
            LocalDateTime ldt = LocalDateTime.parse(attachment.getUploadedAt());
            ps.setTimestamp(5, Timestamp.valueOf(ldt));
            ps.executeUpdate();
        }
        return  attachment;
    }

    public void updateAttachment(Attachment attachment)  throws Exception {
        List<String> errors = attachment.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
        validationUtils.validateAttachmentExists(attachment.getAttachmentId(),"Attachment");
        validationUtils.validateIssueExists(attachment.getIssueId(),"Issue");
        attachment.setUploadedAt(java.time.LocalDateTime.now().toString());

        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.UPDATE_ATTACHMENT)) {
            ps.setString(1, attachment.getFilename());
            ps.setString(2, attachment.getFileUrl());
            LocalDateTime ldt = LocalDateTime.parse(attachment.getUploadedAt());
            ps.setTimestamp(3, Timestamp.valueOf(ldt));
            ps.setString(4, attachment.getAttachmentId().toString());
            ps.executeUpdate();
        }
    }

    public void deleteAttachment(UUID attachmentID)  throws Exception {
        validationUtils.validateAttachmentExists(attachmentID,"Attachment");
        try(Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL.DELETE_ATTACHMENT)) {
            ps.setString(1, attachmentID.toString());
            ps.executeUpdate();
        }
    }

    public List<Attachment> getAttachmentsByIssueId(UUID issueId) throws Exception {
        validationUtils.validateIssueExists(issueId,"Issue");
        List<Attachment> attachments = new ArrayList<>();
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
        public static final String ALL_ATTACHMENTS = "SELECT * FROM attachments";
        public static final String ATTACHMENT_BY_ID = "SELECT * FROM attachments WHERE attachment_id = ?";
        public static final String CREATE_ATTACHMENT =
                " INSERT INTO attachments (attachment_id, issue_id, filename, file_url, uploaded_at ) VALUES (?,?,?,?,?)";
        public static final String UPDATE_ATTACHMENT =
                " UPDATE attachments SET filename=?, file_url=?, uploaded_at = ? where attachment_id = ?";
        public static final String DELETE_ATTACHMENT = " DELETE FROM attachments WHERE attachment_id = ?";
        public static final String ATTACHMENT_BY_ISSUE_ID ="SELECT * FROM attachments WHERE issue_id = ?";
    }
}
