package resource;

import common.AbstractResource;
import domain.Issue;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.IssueLabelServiceImpl;
import service.IssueService;
import service.IssueServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueResource extends AbstractResource {

    private final IssueService issueService = new IssueServiceImpl();

    @GET
    public Response getAllIssues() {
        try{
            List<Issue> issues = issueService.getAllIssues();
            return Response.status(Response.Status.OK).entity(issues).build();
        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get all issues"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getIssueById(@PathParam("id") String id) {
        try{
            Issue issue = issueService.getIssueById(UUID.fromString(id));
            if(issue == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Issue not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(issue).build();
        }catch (IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issue"))
                    .build();
        }
    }

    @POST
    public Response createIssue(String payload) {
        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Issue issue = gson().fromJson(payload, Issue.class);
            Issue createdIssue= issueService.createIssue(issue);
            return Response.status(Response.Status.CREATED).entity(createdIssue).build();
        }catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed:", "");
                List<String> errors = Arrays.asList(errorList.split(", "));
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", e.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create issue"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateIssue(@PathParam("id") String id, String payload) {
        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Issue issue = gson().fromJson(payload, Issue.class);
            issue.setIssueId(UUID.fromString(id));
            issueService.updateIssue(issue);
            return Response.ok(Map.of("message", "Issue updated successfully")).build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to update issue")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteIssue(@PathParam("id") String id) {
        try{
            UUID uuid = UUID.fromString(id);
            issueService.deleteIssue(uuid);
            return Response.ok(Map.of("message", "Issue deleted successfully")).build();
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
                    .entity(Map.of("error", "Failed to delete issue"))
                    .build();
        }
    }

    @GET
    @Path("/project/{project_id}")
    public Response getIssuesByProjectId(@PathParam("project_id") String projectId) {
        try {
            List<Issue> issues = issueService.getIssueForProject(UUID.fromString(projectId));
            return Response.ok(issues).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issues"))
                    .build();
        }
    }

    @GET
    @Path("/reporter/{reporterId}")
    public Response getIssuesByReporter(@PathParam("reporterId") String reporterId) {
        try {
            List<Issue> issues = issueService.getIssuesByReporter(UUID.fromString(reporterId));
            return Response.ok(issues).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issues"))
                    .build();
        }
    }

    @GET
    @Path("/assignee/{assigneeId}")
    public Response getIssuesByAssignee(@PathParam("assigneeId") String assigneeId) {
        try {
            List<Issue> issues = issueService.getIssuesByAssignee(UUID.fromString(assigneeId));
            return Response.ok(issues).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issues"))
                    .build();
        }
    }

    @GET
    @Path("/status/{statusId}")
    public Response getIssuesByStatus(@PathParam("statusId") int statusId) {
        try {
            List<Issue> issues = issueService.getIssuesByStatus(statusId);
            return Response.ok(issues).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issues"))
                    .build();
        }
    }

    @GET
    @Path("/priority/{priorityId}")
    public Response getIssuesByPriority(@PathParam("priorityId") int priorityId) {
        try {
            List<Issue> issues = issueService.getIssuesByPriority(priorityId);
            return Response.ok(issues).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve issues"))
                    .build();
        }
    }

    @GET
    @Path("/search/{title}")
    public Response searchIssuesByTitle(@PathParam("title") String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Title is required"))
                        .build();
            }
            List<Issue> issues = issueService.searchIssuesByTitle(title);
            return Response.ok(issues).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to search issues"))
                    .build();
        }
    }


    //-------------------------------for issue label mapping------------------------------------------

    private final IssueLabelServiceImpl labelService = new IssueLabelServiceImpl();

    // POST /api/issues/{issueId}/labels/{labelId} - assign label
    @POST
    @Path("/{issueId}/labels/{labelId}")
    public Response assignLabel(@PathParam("issueId") String issueId, @PathParam("labelId") String labelId) {
        try {
            labelService.addLabelToIssue(UUID.fromString(issueId), UUID.fromString(labelId));
            return Response.status(Response.Status.CREATED).entity(Map.of("message", "Label assigned")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to assign label")).build();
        }
    }

    // DELETE /api/issues/{issueId}/labels/{labelId} - remove label
    @DELETE
    @Path("/{issueId}/labels/{labelId}")
    public Response removeLabel(@PathParam("issueId") String issueId, @PathParam("labelId") String labelId) {
        try {
            labelService.removeLabelFromIssue(UUID.fromString(issueId), UUID.fromString(labelId));
            return Response.ok(Map.of("message", "Label removed")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to remove label")).build();
        }
    }

    // GET /api/issues/{issueId}/labels - get all labels for issue
    @GET
    @Path("/{issueId}/labels")
    public Response getLabelsForIssue(@PathParam("issueId") String issueId) {
        try {
            return Response.ok(labelService.getLabelsForIssue(UUID.fromString(issueId))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Failed to fetch labels for issue")).build();
        }
    }

}
