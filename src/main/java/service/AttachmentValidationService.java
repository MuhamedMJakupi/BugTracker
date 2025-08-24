package service;

import domain.Attachment;

import java.util.ArrayList;
import java.util.List;

public class AttachmentValidationService {

    public List<String> validateAttachment(Attachment attachment)  {
        List<String> errors = new ArrayList<>();

        if(attachment.getIssueId()==null) {
            errors.add("Issue Id is required");
        }
        if(attachment.getFilename()==null || attachment.getFilename().trim().isEmpty()) {
            errors.add("Filename is required");
        }
        if (attachment.getFileUrl() == null || attachment.getFileUrl().trim().isEmpty()) {
            errors.add("File Url is required");
        }
        return  errors;
    }

    public List<String> validateAttachmentForUpdate(Attachment attachment)  {
        List<String> errors = validateAttachment(attachment);

        if(attachment.getAttachmentId()==null) {
            errors.add("Attachment Id is required");
        }
        return  errors;
    }

    public List<String> validateAttachmentForCreation(Attachment attachment)  {
        List<String> errors = validateAttachment(attachment);
        if(attachment.getAttachmentId()!=null) {
            errors.add("Attachment ID should not be provided for new attachments");
        }
        if (attachment.getUploadedAt() != null) {
            errors.add("Uploaded timestamp should not be provided for new attachments");
        }
        return  errors;
    }
}
