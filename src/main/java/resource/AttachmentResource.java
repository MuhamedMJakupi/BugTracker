package resource;

import common.AbstractResource;
import domain.Attachment;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AttachmentService;
import service.AttachmentServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/attachments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttachmentResource extends AbstractResource {

    private final AttachmentService attachmentService = new AttachmentServiceImpl();

    @GET
    public Response getAllAttachments() throws Exception {
            List<Attachment> attachments = attachmentService.getAllAttachments();
            return Response.ok(attachments).build();
    }

    @GET
    @Path("/{id}")
    public Response getAttachmentById(@PathParam("id") String id) throws Exception {
            Attachment attachment = attachmentService.getAttachmentById(UUID.fromString(id));
            if (attachment == null) {
                throw new IllegalArgumentException("Attachment not found");
            }
            return Response.ok(attachment).build();
    }

    @POST
    public Response createAttachment(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Attachment attachment = gson().fromJson(payload, Attachment.class);
            Attachment createdAttachment = attachmentService.createAttachment(attachment);
            return Response.status(Response.Status.CREATED).entity(createdAttachment).build();
    }

    @PUT
    @Path("/{id}")

    public Response updateAttachment(@PathParam("id") String id, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Attachment attachment = gson().fromJson(payload, Attachment.class);
            attachment.setAttachmentId(UUID.fromString(id));
            attachmentService.updateAttachment(attachment);
            return Response.ok(Map.of("message", "Attachment updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAttachment(@PathParam("id") String id) throws Exception {
            UUID uuid = UUID.fromString(id);
            attachmentService.deleteAttachment(uuid);
            return Response.ok(Map.of("message", "Attachment deleted successfully")).build();
    }

    @GET
    @Path("/issue/{issue_id}")
    public Response getAttachmentByIssueId(@PathParam("issue_id") String issue_id) throws Exception {
            List<Attachment> attachments = attachmentService.getAttachmentsByIssueId(UUID.fromString(issue_id));
            return Response.ok(attachments).build();
   }
}
