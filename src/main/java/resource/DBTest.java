package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;

import static common.AbstractService.getConnection;

@Path("/dbtest")
public class DBTest {

    @GET
    @Produces(MediaType.APPLICATION_JSON)// for test
    public Response checkDb() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                return Response.ok("DB Connection Successful").build();
            }
        } catch (Exception e) {
            return Response.serverError().entity("DB Connection Failed: " + e.getMessage()).build();
        }
        return Response.serverError().entity("Unknown Error").build();
    }
}
