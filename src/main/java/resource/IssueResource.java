package resource;

import com.google.gson.JsonObject;
import common.AbstractResource;
import domain.Issue;
import domain.IssueHistory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.IssueLabelServiceImpl;
import service.IssueService;
import service.IssueServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueResource extends AbstractResource {

    private final IssueService issueService = new IssueServiceImpl();

    @GET
    public Response getAllIssues() throws Exception {
            List<Issue> issues = issueService.getAllIssues();
            return Response.ok(issues).build();
    }

    @GET
    @Path("/{id}")
    public Response getIssueById(@PathParam("id") String id) throws Exception {
            Issue issue = issueService.getIssueById(UUID.fromString(id));
            return Response.ok(issue).build();
    }

    @POST
    public Response createIssue(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Issue issue = gson().fromJson(payload, Issue.class);
            Issue createdIssue= issueService.createIssue(issue);
            return Response.status(Response.Status.CREATED).entity(createdIssue).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateIssue(@PathParam("id") String id, String payload) throws Exception {
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Request body is required");
        }

        JsonObject json = gson().fromJson(payload, JsonObject.class);

        if (!json.has("changedByUserId")) {
            throw new IllegalArgumentException("changedByUserId is required");
        }

        String changedByUserId = json.get("changedByUserId").getAsString();
        json.remove("changedByUserId");

        Issue issue = gson().fromJson(json, Issue.class);
        issue.setIssueId(UUID.fromString(id));

        issueService.updateIssue(issue, UUID.fromString(changedByUserId));
        return Response.ok(Map.of("message", "Issue updated successfully")).build();

    }

    @DELETE
    @Path("/{id}")
    public Response deleteIssue(@PathParam("id") String id) throws Exception {
            UUID uuid = UUID.fromString(id);
            issueService.deleteIssue(uuid);
            return Response.ok(Map.of("message", "Issue deleted successfully")).build();
    }

    @GET
    @Path("/project/{project_id}")
    public Response getIssuesByProjectId(@PathParam("project_id") String projectId) throws Exception {
            List<Issue> issues = issueService.getIssueForProject(UUID.fromString(projectId));
            return Response.ok(issues).build();
    }

    @GET
    @Path("/reporter/{reporterId}")
    public Response getIssuesByReporter(@PathParam("reporterId") String reporterId) throws Exception {
            List<Issue> issues = issueService.getIssuesByReporter(UUID.fromString(reporterId));
            return Response.ok(issues).build();
    }

    @GET
    @Path("/assignee/{assigneeId}")
    public Response getIssuesByAssignee(@PathParam("assigneeId") String assigneeId) throws Exception {
            List<Issue> issues = issueService.getIssuesByAssignee(UUID.fromString(assigneeId));
            return Response.ok(issues).build();
    }

    @GET
    @Path("/status/{statusId}")
    public Response getIssuesByStatus(@PathParam("statusId") int statusId) throws Exception {
            List<Issue> issues = issueService.getIssuesByStatus(statusId);
            return Response.ok(issues).build();
    }

    @GET
    @Path("/priority/{priorityId}")
    public Response getIssuesByPriority(@PathParam("priorityId") int priorityId) throws Exception {
            List<Issue> issues = issueService.getIssuesByPriority(priorityId);
            return Response.ok(issues).build();
    }

    @GET
    @Path("/search/{title}")
    public Response searchIssuesByTitle(@PathParam("title") String title) throws Exception {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            List<Issue> issues = issueService.searchIssuesByTitle(title);
            return Response.ok(issues).build();
    }

    @GET
    @Path("/priorities")
    public Response getAllPriorities() throws Exception {
        List<Map<String, Object>> priorities = issueService.getAllPriorities();
        return Response.ok(priorities).build();
    }

    @GET
    @Path("/statuses")
    public Response getAllStatuses() throws Exception {
        List<Map<String, Object>> statuses = issueService.getAllStatuses();
        return Response.ok(statuses).build();
    }


    //-------------------------------for issue label mapping------------------------------------------

    private final IssueLabelServiceImpl labelService = new IssueLabelServiceImpl();

    @POST
    @Path("/{issueId}/labels/{labelId}")
    public Response assignLabel(@PathParam("issueId") String issueId, @PathParam("labelId") String labelId) throws Exception {
            labelService.addLabelToIssue(UUID.fromString(issueId), UUID.fromString(labelId));
            return Response.status(Response.Status.CREATED).entity(Map.of("message", "Label assigned")).build();
    }

    @DELETE
    @Path("/{issueId}/labels/{labelId}")
    public Response removeLabel(@PathParam("issueId") String issueId, @PathParam("labelId") String labelId) throws Exception {
            labelService.removeLabelFromIssue(UUID.fromString(issueId), UUID.fromString(labelId));
            return Response.ok(Map.of("message", "Label removed")).build();
    }

    @GET
    @Path("/{issueId}/labels")
    public Response getLabelsForIssue(@PathParam("issueId") String issueId) throws Exception {
            return Response.ok(labelService.getLabelsForIssue(UUID.fromString(issueId))).build();
    }

    //-------------------------History------------------------------

    @GET
    @Path("/{id}/history")
    public Response getIssueHistory(@PathParam("id") String id) throws Exception {
            UUID issueId = UUID.fromString(id);
            List<IssueHistory> history = issueService.getIssueHistory(issueId);
            return Response.ok(history).build();
    }


    //---------- Down are 2 different methods of update, separately with/without history track --------------------

    //    @PUT
//    @Path("/{id}")
//    public Response updateIssue(@PathParam("id") String id, String payload) {
//        try{
//            if (payload == null || payload.trim().isEmpty()) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", "Request body is required"))
//                        .build();
//            }
//            Issue issue = gson().fromJson(payload, Issue.class);
//            issue.setIssueId(UUID.fromString(id));
//            issueService.updateIssue(issue);
//            return Response.ok(Map.of("message", "Issue updated successfully")).build();
//        }catch (IllegalArgumentException e) {
//            String message = e.getMessage();
//            if (message.startsWith("Validation failed:")) {
//                String errorList = message.replace("Validation failed: ", "");
//                List<String> errors = Arrays.asList(errorList.split(", "));
//                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
//            } else if (message.toLowerCase().contains("uuid")) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", "Invalid UUID format"))
//                        .build();
//            } else {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", e.getMessage()))
//                        .build();
//            }
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to update issue")).build();
//        }
//    }

//    @PUT
//    @Path("/{id}/with-history")
//    public Response updateIssueWithHistory(@PathParam("id") String id, String payload) {
//        try {
//            if (payload == null || payload.trim().isEmpty()) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", "Request body is required"))
//                        .build();
//            }
//
//            JsonObject json = gson().fromJson(payload, JsonObject.class);
//
//            if (!json.has("changedByUserId")) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", "changedByUserId is required"))
//                        .build();
//            }
//
//            String changedByUserId = json.get("changedByUserId").getAsString();
//
//            json.remove("changedByUserId");
//            Issue issue = gson().fromJson(json, Issue.class);
//            issue.setIssueId(UUID.fromString(id));
//
//            issueService.updateIssueWithHistory(issue, UUID.fromString(changedByUserId));
//
//            return Response.ok(Map.of("message", "Issue updated with history tracking")).build();
//
//        } catch (IllegalArgumentException e) {
//            String message = e.getMessage();
//            if (message.startsWith("Validation failed:")) {
//                String errorList = message.replace("Validation failed: ", "");
//                List<String> errors = Arrays.asList(errorList.split(", "));
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("errors", errors)).build();
//            } else if (message.toLowerCase().contains("uuid")) {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", "Invalid UUID format"))
//                        .build();
//            } else {
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity(Map.of("error", e.getMessage()))
//                        .build();
//            }
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(Map.of("error", "Failed to update issue with history"))
//                    .build();
//        }
//    }


}
