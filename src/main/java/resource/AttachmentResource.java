package resource;

import common.AbstractResource;
import domain.Attachment;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AttachmentService;
import service.AttachmentServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/attachments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttachmentResource extends AbstractResource {

    private final AttachmentService attachmentService = new AttachmentServiceImpl();

    @GET
    public Response getAllAttachments() {
        try {
            List<Attachment> attachments = attachmentService.getAllAttachments();
            return Response.status(Response.Status.OK).entity(attachments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get all Attachments"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getAttachmentById(@PathParam("id") String id) {
        try {
            Attachment attachment = attachmentService.getAttachmentById(UUID.fromString(id));
            if (attachment == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Attachment not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(attachment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve attachment"))
                    .build();
        }
    }

    @POST
    public Response createAttachment(String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Attachment attachment = gson().fromJson(payload, Attachment.class);
            Attachment createdAttachment = attachmentService.createAttachment(attachment);
            return Response.status(Response.Status.CREATED).entity(createdAttachment).build();
        } catch (IllegalArgumentException e) {
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
                    .entity(Map.of("error", "Failed to create attachment"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")

    public Response updateAttachment(@PathParam("id") String id, String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Attachment attachment = gson().fromJson(payload, Attachment.class);
            attachment.setAttachmentId(UUID.fromString(id));

            attachmentService.updateAttachment(attachment);
            return Response.ok(Map.of("message", "Attachment updated successfully")).build();
        } catch (IllegalArgumentException e) {
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to update Attachment")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAttachment(@PathParam("id") String id) {
        try{
            UUID uuid = UUID.fromString(id);
            attachmentService.deleteAttachment(uuid);
            return Response.ok(Map.of("message", "Attachment deleted successfully")).build();
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
                    .entity(Map.of("error", "Failed to delete attachment"))
                    .build();
        }
    }

    @GET
    @Path("/issue/{issue_id}")
    public Response getAttachmentByIssueId(@PathParam("issue_id") String issue_id) {
        try{
            List<Attachment> attachments = attachmentService.getAttachmentsByIssueId(UUID.fromString(issue_id));
            return Response.ok(attachments).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve attachments"))
                    .build();
        }
    }
}
