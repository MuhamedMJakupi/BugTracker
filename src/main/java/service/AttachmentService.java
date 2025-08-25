package service;

import domain.Attachment;
import java.util.List;
import java.util.UUID;

public interface AttachmentService {

    Attachment createAttachment(Attachment attachment) throws Exception;
    void updateAttachment(Attachment attachment) throws Exception;
    void deleteAttachment(UUID attachmentId) throws Exception;
    Attachment getAttachmentById(UUID attachmentId) throws Exception;
    List<Attachment> getAllAttachments() throws Exception;

    List<Attachment> getAttachmentsByIssueId(UUID issueId) throws Exception;
}
