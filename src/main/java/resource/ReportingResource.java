package resource;

import common.AbstractResource;
import service.ReportingService;
import service.ReportingServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportingResource extends AbstractResource {

    private final ReportingService reportingService = new ReportingServiceImpl();

    @GET
    @Path("/dashboard")
    public Response getDashboardStats() throws Exception {
            Map<String, Object> stats = reportingService.getDashboardStats();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/issues/status")
    public Response getIssuesByStatus() throws Exception {
            Map<String, Integer> stats = reportingService.getIssueCountsByStatus();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/issues/priority")
    public Response getIssuesByPriority() throws Exception {
            Map<String, Integer> stats = reportingService.getIssueCountsByPriority();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/issues/assignee")
    public Response getIssuesByAssignee() throws Exception {
            Map<String, Integer> stats = reportingService.getIssueCountsByAssignee();
            return Response.ok(stats).build();
    }
    @GET
    @Path("/issues/reporter")
    public Response getIssuesByReporter() throws Exception {
            Map<String, Integer> stats = reportingService.getIssueCountsByReporter();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/projects/{projectId}/stats")
    public Response getProjectStats(@PathParam("projectId") String projectId) throws Exception {
            UUID projectUuid = UUID.fromString(projectId);
            Map<String, Integer> stats = reportingService.getProjectIssueStats(projectUuid);
            return Response.ok(stats).build();
    }

    @GET
    @Path("/performance/resolution-time")
    public Response getAverageResolutionTime() throws Exception {
            Double avgTime = reportingService.getAverageTimeToResolution();
            return Response.ok(Map.of("averageResolutionDays", avgTime)).build();
    }

    @GET
    @Path("/trends/created")
    public Response getIssuesCreatedByMonth() throws Exception {
            Map<String, Integer> stats = reportingService.getIssuesCreatedByMonth();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/trends/resolved")
    public Response getIssuesResolvedByMonth() throws Exception {
            Map<String, Integer> stats = reportingService.getIssuesResolvedByMonth();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/productivity/resolved")
    public Response getProductivityByUser() throws Exception {
            Map<String, Integer> stats = reportingService.getIssuesResolvedByUser();
            return Response.ok(stats).build();
    }

    @GET
    @Path("/workload/open")
    public Response getOpenIssuesByUser() throws Exception {
            Map<String, Integer> stats = reportingService.getOpenIssuesByUser();
            return Response.ok(stats).build();
    }
}
