package resource;

import common.AbstractResource;
import domain.User;
import service.UserService;
import service.UserServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractResource {

    private final UserService userService = new UserServiceImpl();

    @GET
    public Response getAllUsers() throws Exception {
            List<User> users = userService.getAllUsers();
            return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) throws Exception {
            User user = userService.getUserById(UUID.fromString(id));
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return Response.ok(user).build();
    }

    @POST
    public Response createUser(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            User user = gson().fromJson(payload, User.class);
            User createdUser = userService.createUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
            User user = gson().fromJson(payload, User.class);
            user.setUserId(UUID.fromString(id));
            userService.updateUser(user);
            return Response.ok(Map.of("message", "User updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) throws Exception {
            UUID uuid = UUID.fromString(id);
            userService.deleteUser(uuid);
            return Response.ok(Map.of("message", "User deleted successfully")).build();
    }

    @POST
    @Path("/login")
    public Response authenticateUser(String payload) throws Exception {
            if (payload == null || payload.trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }

            User loginUser = gson().fromJson(payload, User.class);
            if (loginUser == null) {
                throw new IllegalArgumentException("User not found");
            }

            if (loginUser.getEmail() == null || loginUser.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            if (loginUser.getPasswordHash() == null || loginUser.getPasswordHash().trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }

            boolean isAuthenticated = userService.authenticateUser(
                    loginUser.getEmail().trim(),
                    loginUser.getPasswordHash()
            );

            if (isAuthenticated) {
                return Response.ok(Map.of("message", "Authentication successful")).build();
            } else {
                throw new IllegalArgumentException("Invalid email or password");
            }
    }
}

