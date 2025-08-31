package service;

import domain.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentService {

    List<Comment> getAllComments() throws Exception;
    Comment getCommentById(UUID commentId) throws Exception;
    Comment createComment(Comment comment) throws Exception;
    void updateComment(Comment comment) throws Exception;
    void deleteComment(UUID commentId) throws Exception;

    List<Comment> getCommentsByIssueId(UUID issueId) throws Exception;
    List<Comment> getCommentsByUserId(UUID userId) throws Exception;
}
