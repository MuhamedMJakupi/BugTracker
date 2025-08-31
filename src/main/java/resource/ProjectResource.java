package resource;

import common.AbstractResource;
import domain.Project;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.ProjectService;
import service.ProjectServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource extends AbstractResource {

    private final ProjectService projectService = new ProjectServiceImpl();

    @GET
    public Response getAllProjects() throws Exception {
            List<Project> projects = projectService.getAllProjects();
            return Response.ok(projects).build();
    }

    @GET
    @Path("/{id}")
    public Response getProjectById(@PathParam("id") String id) throws Exception {
            Project project = projectService.getProjectById(UUID.fromString(id));
            if (project == null) {
                throw new IllegalArgumentException("Project not found");
            }
            return Response.ok(project).build();
    }

    @POST
    public Response createProject(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Project project = gson().fromJson(payload, Project.class);
            Project createdProject = projectService.createProject(project);
            return Response.status(Response.Status.CREATED).entity(createdProject).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProject(@PathParam("id") String id, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Project project = gson().fromJson(payload, Project.class);
            project.setProjectId(UUID.fromString(id));
            projectService.updateProject(project);
            return Response.ok(Map.of("message", "Project updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") String id) throws Exception {
            UUID uuid = UUID.fromString(id);
            projectService.deleteProject(uuid);
            return Response.ok(Map.of("message", "Project deleted successfully")).build();
    }

    @GET
    @Path("/name/{projectName}")
    public Response getProjectByName(@PathParam("projectName") String projectName) throws Exception {
        List<Project> projects = projectService.getProjectByName(projectName);
        if (projects == null || projects.isEmpty()) {
            throw new IllegalArgumentException("Project not found");
        }
        return Response.ok(projects).build();
    }

    @GET
    @Path("/owner/{ownerId}")
    public Response getProjectByOwner(@PathParam("ownerId") String ownerId) throws Exception {
            List<Project> projects = projectService.getProjectsByOwner(UUID.fromString(ownerId));
            return Response.ok(projects).build();
    }
}
