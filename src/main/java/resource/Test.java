package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ping")
public class Test {

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)// for test
    public Response ping() {
        return Response.ok(" REST is working").build();
    }
}