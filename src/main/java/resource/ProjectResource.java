package resource;

import common.AbstractResource;
import domain.Project;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.ProjectServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource extends AbstractResource {

    private final ProjectServiceImpl projectService = new ProjectServiceImpl();

    @GET
    public Response getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            return Response.status(Response.Status.OK).entity(projects).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get projects"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getProjectById(@PathParam("id") String id) {
        try {
            Project project = projectService.getProjectById(UUID.fromString(id));
            if (project == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Project not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(project).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve project"))
                    .build();
        }
    }

    @POST
    public Response createProject(String payload) {

        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Project project = gson().fromJson(payload, Project.class);
            Project createdProject = projectService.createProject(project);
            return Response.status(Response.Status.CREATED).entity(createdProject).build();
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
                    .entity(Map.of("error", "Failed to create project"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateProject(@PathParam("id") String id, String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Project project = gson().fromJson(payload, Project.class);
            project.setProjectId(UUID.fromString(id));
            projectService.updateProject(project);
            return Response.ok(Map.of("message", "Project updated successfully")).build();
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorLost = message.replace("Validation failed:", "");
                List<String> errors = Arrays.asList(errorLost.split(", "));
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to update Project")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            projectService.deleteProject(uuid);
            return Response.ok(Map.of("message", "Project deleted successfully")).build();
        } catch (IllegalArgumentException e) {
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
                    .entity(Map.of("error", "Failed to delete project"))
                    .build();
        }
    }

    @GET
    @Path("/owner/{ownerId}")
    public Response getProjectByOwner(@PathParam("ownerId") String ownerId) {
        try{
            List<Project> projects = projectService.getProjectsByOwner(UUID.fromString(ownerId));
            return Response.status(Response.Status.OK).entity(projects).build();
        } catch (IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error","Invalid UUID format")).build();
        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to retrieve projects")).build();
        }
    }

    //you need to add %20 if it has more than 1 name like Mobile%20App
    //changed the query with like and % no need for 20% get all like...
    @GET
    @Path("/name/{projectName}")
    public Response getProjectByName(@PathParam("projectName") String projectName) {
        try{
            List<Project> projects = projectService.getProjectByName(projectName);
            if (projects == null || projects.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Project not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(projects).build();
        } catch (IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error","Invalid project name")).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to retrieve project")).build();
        }
    }
}
