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
    public Response getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve users"))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) {
        try {
            User user = userService.getUserById(UUID.fromString(id));
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "User not found"))
                        .build();
            }
            return Response.ok(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to retrieve user"))
                    .build();
        }
    }

    @POST
    public Response createUser(String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            User user = gson().fromJson(payload, User.class);
            User createdUser = userService.createUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed: ", "");
                List<String> errors = Arrays.asList(errorList.split(", "));
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("errors", errors))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", e.getMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create user"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, String payload) {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }
            User user = gson().fromJson(payload, User.class);
            user.setUserId(UUID.fromString(id));

            userService.updateUser(user);
            return Response.ok(Map.of("message", "User updated successfully")).build();

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed: ", "");
                List<String> errors = Arrays.asList(errorList.split(", "));
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("errors", errors))
                        .build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to update user"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            userService.deleteUser(uuid);
            return Response.ok(Map.of("message", "User deleted successfully")).build();

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
                    .entity(Map.of("error", "Failed to delete user"))
                    .build();
        }
    }
}

