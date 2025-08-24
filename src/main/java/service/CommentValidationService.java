package service;

import domain.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentValidationService {

    public List<String> validateComments(Comment comment) {
        List<String> errors = new ArrayList<>();

        if(comment.getIssueId()==null) {
            errors.add("Issue id is mandatory");
        }
        if (comment.getUserId() == null) {
            errors.add("User id is mandatory");
        }
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            errors.add("Comment text is mandatory");
        }else if (comment.getText().length() > 1000) {
            errors.add("Comment text cannot exceed 1000 characters");
        }

        return errors;

    }

    public List<String>  validateCommentForUpdate(Comment comment) {
        List<String> errors = validateComments(comment);

        if(comment.getCommentId()==null) {
            errors.add("Comment ID is required for updates");
        }
        return errors;

    }
    public List<String>  validateCommentForCreation(Comment comment) {
        List<String> errors = validateComments(comment);

        if(comment.getCommentId()!=null) {
            errors.add("Comment ID should not be provided for creation");
        }
        if (comment.getTimestamp() != null) {
            errors.add("Created timestamp should not be provided for new comments");
        }
        return errors;
    }
}
