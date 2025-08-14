package resource;

import common.AbstractResource;
import domain.User;
import service.UserService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractResource {

    private final UserService userService = new UserService();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @POST
    public Response createUser(String payload) {
        try {
            // 1. Parse JSON to User object
            User user = gson().fromJson(payload, User.class);

            // 2. Manually apply Bean Validation
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("errors", errors))
                        .build();
            }

            // 3. Set ID & timestamp
            user.setUserId(UUID.randomUUID());
            user.setCreatedAt(java.time.LocalDateTime.now());

            // 4. Call service (DB validation + save)
            User createdUser = userService.createUser(user);

            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Internal server error"))
                    .build();
        }
    }

    @GET
    public Response getAllUsers() {
        try {
            return Response.ok(userService.getAllUsers()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Internal server error"))
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
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Internal server error"))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, String payload) {
        try {
            User user = gson().fromJson(payload, User.class);
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
            }
            user.setUserId(UUID.fromString(id));
            userService.updateUser(user);
            return Response.ok(Map.of("message", "User updated successfully")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Internal server error"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        try {
            userService.deleteUser(UUID.fromString(id));
            return Response.ok(Map.of("message", "User deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Internal server error"))
                    .build();
        }
    }
}
