package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Attachment {
    private UUID attachmentId;
    private UUID issueId;
    private String filename;
    private String fileUrl;
    private LocalDateTime uploadedAt;

    public Attachment() {}

    public Attachment(UUID issueId, String filename, String fileUrl) {
        this.attachmentId = UUID.randomUUID();
        this.uploadedAt = LocalDateTime.now();

        this.issueId = issueId;
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    public UUID getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(UUID attachmentId) {
        this.attachmentId = attachmentId;
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
                "attachmentId=" + attachmentId +
                ", issueId=" + issueId +
                ", filename='" + filename + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}


