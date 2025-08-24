package resource;

import common.AbstractResource;
import domain.Comment;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.CommentService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource extends AbstractResource {

    private final CommentService commentService = new CommentService();

    @GET
    public Response getAllComments() {
        try{
            List<Comment> comments = commentService.getAllComments();
            return Response.status(Response.Status.OK).entity(comments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(Map.of("error", "Failed to get all comments"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
        public Response getCommentById(@PathParam("id") String id) {
        try{
            Comment comment = commentService.getCommentById(UUID.fromString(id));
            if (comment == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error","Comment not found")).
                        build();
            }
            return Response.status(Response.Status.OK).entity(comment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve comment by id"))
                    .build();
        }
    }

    @POST
    public Response createComment(String payload) {
        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Comment comment = gson().fromJson(payload, Comment.class);
            Comment createdComment = commentService.createComment(comment);
            return Response.status(Response.Status.CREATED).entity(createdComment).build();
        }catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed: ", "");
                List<String> errors = Arrays.asList(errorList.split(", "));
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", e.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create comment"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateComment(@PathParam("id") String id, String payload) {
        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Comment comment = gson().fromJson(payload, Comment.class);
            comment.setCommentId(UUID.fromString(id));

            commentService.updateComment(comment);
            return Response.ok(Map.of("message", "Comment updated successfully")).build();
        }catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed: ", "");
                List<String> errors = Arrays.asList(errorList.split(", "));
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
            } else if (message.toLowerCase().contains("uuid")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid UUID format"))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", e.getMessage()))
                        .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to update Comment")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            commentService.deleteComment(uuid);
            return Response.ok(Map.of("message", "Comment deleted successfully")).build();
        }catch (IllegalArgumentException e) {
            if (e.getMessage().toLowerCase().contains("uuid")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid UUID format"))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", e.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete comment"))
                    .build();
        }
    }

    @GET
    @Path("/issue/{issue_id}")
    public Response getCommentByIssueId(@PathParam("issue_id") String issueId) {
        try{
            List<Comment> comments = commentService.getCommentsByIssueId(UUID.fromString(issueId));
            return Response.ok(comments).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve comment"))
                    .build();
        }
    }

    @GET
    @Path("/user/{user_id}")
    public Response getCommentByUserId(@PathParam("user_id") String userId) {
        try{
            List<Comment> comments = commentService.getCommentsByUserId(UUID.fromString(userId));
            return Response.ok(comments).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve comments"))
                    .build();
        }
    }
}
