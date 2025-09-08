package resource;

import common.AbstractResource;
import domain.Comment;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.CommentService;
import service.CommentServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource extends AbstractResource {

    private final CommentService commentService = new CommentServiceImpl();

    @GET
    public Response getAllComments() throws Exception {
            List<Comment> comments = commentService.getAllComments();
            return Response.ok(comments).build();
    }

    @GET
    @Path("/{id}")
        public Response getCommentById(@PathParam("id") String id) throws Exception {
            Comment comment = commentService.getCommentById(UUID.fromString(id));
            return Response.ok(comment).build();
    }

    @POST
    public Response createComment(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");

            }
            Comment comment = gson().fromJson(payload, Comment.class);
            Comment createdComment = commentService.createComment(comment);
            return Response.status(Response.Status.CREATED).entity(createdComment).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateComment(@PathParam("id") String id, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");

            }
            Comment comment = gson().fromJson(payload, Comment.class);
            comment.setCommentId(UUID.fromString(id));
            commentService.updateComment(comment);
            return Response.ok(Map.of("message", "Comment updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") String id) throws Exception {
            commentService.deleteComment(UUID.fromString(id));
            return Response.ok(Map.of("message", "Comment deleted successfully")).build();
    }

    @GET
    @Path("/issue/{issue_id}")
    public Response getCommentByIssueId(@PathParam("issue_id") String issueId) throws Exception {
            List<Comment> comments = commentService.getCommentsByIssueId(UUID.fromString(issueId));
            return Response.ok(comments).build();
    }

    @GET
    @Path("/user/{user_id}")
    public Response getCommentByUserId(@PathParam("user_id") String userId) throws Exception {
            List<Comment> comments = commentService.getCommentsByUserId(UUID.fromString(userId));
            return Response.ok(comments).build();
    }
}
