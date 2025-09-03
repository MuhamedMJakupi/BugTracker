package common;

import jakarta.servlet.ServletException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import com.google.gson.JsonSyntaxException;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {

        if (exception instanceof ServletException servletException) {
            if (servletException.getRootCause() instanceof IllegalArgumentException rootCause) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of("error", rootCause.getMessage()))
                        .build();
            }
        }

        if (exception instanceof IllegalArgumentException) {
            String message = exception.getMessage();

            if (message != null && message.startsWith("Validation failed:")) {
                String errorList = message.replace("Validation failed: ", "");
                List<String> errors = Arrays.asList(errorList.split(", "));

                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of("errors", errors))
                        .build();
            }

            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", exception.getMessage()))
                    .build();
        }

        if (exception instanceof JsonSyntaxException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Invalid JSON format"))
                    .build();
        }

        if (exception.getMessage() != null &&
                exception.getMessage().toLowerCase().contains("uuid")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        }

        System.err.println("Unexpected error occurred:");
        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "An unexpected error occurred"))
                .build();
    }
}
