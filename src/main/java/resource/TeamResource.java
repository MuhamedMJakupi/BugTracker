package resource;

import common.AbstractResource;
import domain.Team;
import domain.TeamMember;
import domain.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.TeamService;
import service.TeamServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource extends AbstractResource {

    private final TeamService teamService = new TeamServiceImpl();

    @GET
    public Response getAllTeams() throws Exception {
            List<Team> teams = teamService.getAllTeams();
            return Response.ok(teams).build();
    }

    @GET
    @Path("/{id}")
    public Response getTeamById(@PathParam("id") String id) throws Exception {
            Team team = teamService.getTeamById(UUID.fromString(id));
            return Response.ok(team).build();
    }

    @POST
    public Response createTeam(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Team team = gson().fromJson(payload, Team.class);
            Team createdTeam = teamService.createTeam(team);
            return Response.status(Response.Status.CREATED).entity(createdTeam).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTeam(@PathParam("id") String id,String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            Team team = gson().fromJson(payload, Team.class);
            team.setTeamId(UUID.fromString(id));
            teamService.updateTeam(team);
            return Response.ok(Map.of("message", "Team updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") String id) throws Exception {
            teamService.deleteTeam(UUID.fromString(id));
            return Response.ok(Map.of("message", "Team deleted successfully")).build();
    }

    @GET
    @Path("/owner/{ownerId}")
    public Response getTeamsByOwner(@PathParam("ownerId") String ownerId) throws Exception {
        List<Team> teams = teamService.getTeamsByOwner(UUID.fromString(ownerId));
        return Response.ok(teams).build();
    }

    @GET
    @Path("/name/{teamName}")
    public Response getTeamByName(@PathParam("teamName") String teamName) throws Exception {
        if(teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }
            List<Team> teams = teamService.getTeamByName(teamName);
            if(teams == null) {
                throw new IllegalArgumentException("Team not found");
            }
            return Response.ok(teams).build();
    }

    @POST
    @Path("/{teamId}/members")
    public Response addTeamMember(@PathParam("teamId") String teamId, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            TeamMember teamMember = gson().fromJson(payload, TeamMember.class);
            UUID teamUuid = UUID.fromString(teamId);
            teamService.addTeamMember(teamUuid, teamMember.getUserId());
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("message", "User added to team successfully"))
                    .build();
    }

    @DELETE
    @Path("/{teamId}/members/{userId}")
    public Response removeTeamMember(@PathParam("teamId") String teamId, @PathParam("userId") String userId) throws Exception {
        teamService.removeTeamMember(UUID.fromString(teamId), UUID.fromString(userId));
        return Response.ok(Map.of("message", "User removed from team successfully")).build();
    }

    @GET
    @Path("/{teamId}/members")
    public Response getTeamMembers(@PathParam("teamId") String teamId) throws Exception {
            List<User> members = teamService.getTeamMembers(UUID.fromString(teamId));
            return Response.ok(members).build();
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserTeams(@PathParam("userId") String userId) throws Exception {
            List<Team> teams = teamService.getUserTeams(UUID.fromString(userId));
            return Response.ok(teams).build();
    }
}
