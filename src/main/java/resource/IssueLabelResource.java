package resource;

import common.AbstractResource;
import domain.IssueLabel;
import service.IssueLabelService;
import service.IssueLabelServiceImpl;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

@Path("/labels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueLabelResource extends AbstractResource {

    private final IssueLabelService labelService = new IssueLabelServiceImpl();

    @GET
    public Response getAllLabels() {
        try {
            return Response.ok(labelService.getAllLabels()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve labels"))
                    .build();
        }
    }
    @GET
    @Path("/{id}")
    public Response getLabelById(@PathParam("id") String id) {
        try {
            UUID labelId = UUID.fromString(id);
            IssueLabel label = labelService.getLabelById(labelId);
            if (label == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Label not found"))
                        .build();
            }
            return Response.ok(label).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve label"))
                    .build();
        }
    }

    @GET
    @Path("/search/{name}")
    public Response searchLabels(@PathParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Search name is required"))
                        .build();
            }
            List<IssueLabel> labels = labelService.searchLabelsByName(name);
            return Response.ok(labels).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to search labels"))
                    .build();
        }
    }

    @POST
    public Response createLabel(String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            IssueLabel label = gson().fromJson(payload, IssueLabel.class);
            IssueLabel created = labelService.createLabel(label);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to create label")).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateLabel(@PathParam("id") String id, String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            IssueLabel label = gson().fromJson(payload, IssueLabel.class);
            label.setLabelId(UUID.fromString(id));
            labelService.updateLabel(label);
            return Response.ok(Map.of("message", "Label updated")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to update label")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLabel(@PathParam("id") String id) {
        try {
            labelService.deleteLabel(UUID.fromString(id));
            return Response.ok(Map.of("message", "Label deleted")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to delete label")).build();
        }
    }
}
