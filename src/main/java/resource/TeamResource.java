package resource;

import common.AbstractResource;
import domain.Team;
import domain.TeamMember;
import domain.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.TeamService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource extends AbstractResource {

    private final TeamService teamService = new TeamService();

    @GET
    public Response getAllTeams() {
        try{
            List<Team> teams = teamService.getAllTeams();
            return Response.status(Response.Status.OK).entity(teams).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get teams"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTeamById(@PathParam("id") String id) {
        try{
            Team team = teamService.getTeamById(UUID.fromString(id));
            if(team == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Team" +
                                " not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(team).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve team"))
                    .build();
        }
    }

    @POST
    public Response createTeam(String payload) {

        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Team team = gson().fromJson(payload, Team.class);
            Team createdTeam = teamService.createTeam(team);
            return Response.status(Response.Status.CREATED).entity(createdTeam).build();
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
                    .entity(Map.of("error", "Failed to create team"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateTeam(@PathParam("id") String id,String payload) {
        try{
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            Team team = gson().fromJson(payload, Team.class);
            team.setTeamId(UUID.fromString(id));

            teamService.updateTeam(team);
            return Response.ok(Map.of("message", "Team updated successfully")).build();

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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to update Team")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") String id) {
        try{
            UUID uuid = UUID.fromString(id);
            teamService.deleteTeam(uuid);
            return Response.ok(Map.of("message", "Team deleted successfully")).build();
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
                    .entity(Map.of("error", "Failed to delete team"))
                    .build();
        }
    }

    @GET
    @Path("/name/{teamName}")
    public Response getTeamByName(@PathParam("teamName") String teamName) {
        try{
            //Team team = teamService.getTeamByName(teamName);
            List<Team> teams = teamService.getTeamByName(teamName);
            if(teams == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Team not found"))
                        .build();
            }
            return Response.status(Response.Status.OK).entity(teams).build();
        }catch (IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error","Invalid team name")).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error","Failed to retrieve team")).build();
        }
    }

    @POST
    @Path("/{teamId}/members")
    public Response addTeamMember(@PathParam("teamId") String teamId, String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            TeamMember teamMember = gson().fromJson(payload, TeamMember.class);

            UUID teamUuid = UUID.fromString(teamId);
            teamService.addTeamMember(teamUuid, teamMember.getUserId());

            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("message", "User added to team successfully"))
                    .build();

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
                    .entity(Map.of("error", "Failed to add team member"))
                    .build();
        }
    }

    @GET
    @Path("/{teamId}/members")
    public Response getTeamMembers(@PathParam("teamId") String teamId) {
        try {
            List<User> members = teamService.getTeamMembers(UUID.fromString(teamId));
            return Response.ok(members).build();
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
                    .entity(Map.of("error", "Failed to retrieve team members"))
                    .build();
        }
    }

    @DELETE
    @Path("/{teamId}/members/{userId}")
    public Response removeTeamMember(@PathParam("teamId") String teamId, @PathParam("userId") String userId) {
        try {
            teamService.removeTeamMember(UUID.fromString(teamId), UUID.fromString(userId));
            return Response.ok(Map.of("message", "User removed from team successfully")).build();
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
                    .entity(Map.of("error", "Failed to remove team member"))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserTeams(@PathParam("userId") String userId) {
        try {
            List<Team> teams = teamService.getUserTeams(UUID.fromString(userId));
            return Response.ok(teams).build();
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
                    .entity(Map.of("error", "Failed to retrieve user teams"))
                    .build();
        }
    }


}
