package domain;

import common.AbstractEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Attachment extends AbstractEntity {

    private UUID issueId;
    private String filename;
    private String fileUrl;
    private LocalDateTime uploadedAt;

    public Attachment() {}

    public Attachment(UUID issueId, String filename, String fileUrl) {

        setAttachmentId(UUID.randomUUID());
        this.uploadedAt = LocalDateTime.now();

        this.issueId = issueId;
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    public UUID getAttachmentId() {
        return getId();
    }

    public void setAttachmentId(UUID attachmentId) {
        setId(attachmentId);
    }

    public UUID getIssueId() {
        return issueId;
    }

    public void setIssueId(UUID issueId) {
        this.issueId = issueId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "attachmentId=" + getAttachmentId() +
                ", issueId=" + issueId +
                ", filename='" + filename + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (issueId == null) {
            errors.add("Issue Id is required");
        }

        if (filename == null || filename.trim().isEmpty()) {
            errors.add("Filename is required");
        } else if (filename.trim().length() > 255) {
            errors.add("Filename cannot be longer than 255 characters");
        }

        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            errors.add("File Url is required");
        } else if (fileUrl.trim().length() > 500) {
            errors.add("File Url cannot be longer than 255 characters");
        }
        return errors;
    }

    @Override
    public List<String> validateForCreation() {
        List<String> errors = validate();

        if (getAttachmentId() != null) {
            errors.add("Attachment ID should not be provided for new attachments");
        }

        if (uploadedAt != null) {
            errors.add("Uploaded timestamp should not be provided for new attachments");
        }
        return errors;
    }

    @Override
    public List<String> validateForUpdate() {
        List<String> errors = validate();

        if (getAttachmentId() == null) {
            errors.add("Attachment Id is required");
        }
        return errors;
    }
}


