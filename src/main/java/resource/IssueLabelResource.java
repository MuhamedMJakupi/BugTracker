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
    public Response getAllLabels() throws Exception {
            return Response.ok(labelService.getAllLabels()).build();
    }
    @GET
    @Path("/{id}")
    public Response getLabelById(@PathParam("id") String id) throws Exception {
            IssueLabel label = labelService.getLabelById(UUID.fromString(id));
            if (label == null) {
                throw new IllegalArgumentException("Label not found");
            }
            return Response.ok(label).build();
    }

    @POST
    public Response createLabel(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            IssueLabel label = gson().fromJson(payload, IssueLabel.class);
            IssueLabel created = labelService.createLabel(label);
            return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateLabel(@PathParam("id") String id, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            IssueLabel label = gson().fromJson(payload, IssueLabel.class);
            label.setLabelId(UUID.fromString(id));
            labelService.updateLabel(label);
            return Response.ok(Map.of("message", "Label updated")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLabel(@PathParam("id") String id) throws Exception {
            labelService.deleteLabel(UUID.fromString(id));
            return Response.ok(Map.of("message", "Label deleted successfully")).build();

    }

    @GET
    @Path("/search/{name}")
    public Response searchLabels(@PathParam("name") String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        List<IssueLabel> labels = labelService.searchLabelsByName(name);
        return Response.ok(labels).build();
    }
}
