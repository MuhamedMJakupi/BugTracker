//package common;
//
//import jakarta.annotation.Priority;
//import jakarta.servlet.ServletException;
//import jakarta.ws.rs.ext.ExceptionMapper;
//import jakarta.ws.rs.ext.Provider;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.MediaType;
//import java.util.Map;
//import java.util.Arrays;
//import java.util.List;
//import com.google.gson.JsonSyntaxException;
//
//@Provider
//@Priority(1)
//public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
//
//    public GlobalExceptionMapper() {
//        System.out.println("GlobalExceptionMapper initialized successfully");
//    }
//    @Override
//    public Response toResponse(Throwable exception) {
//
//        System.err.println("GlobalExceptionMapper triggered: " + exception.getClass().getSimpleName() + " - " + exception.getMessage());
//
//        Throwable root = unwrap(exception);
//
//        if (root instanceof ServletException || exception instanceof ServletException) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .type(MediaType.APPLICATION_JSON)
//                    .entity(Map.of("error", "Servlet exception: " + root.getMessage()))
//                    .build();
//        }
//
//        if (root instanceof IllegalArgumentException || exception instanceof IllegalArgumentException) {
//            String message = root.getMessage();
//
//            if (message != null && message.startsWith("Validation failed:")) {
//                String errorList = message.replace("Validation failed: ", "");
//                List<String> errors = Arrays.asList(errorList.split(", "));
//
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .type(MediaType.APPLICATION_JSON)
//                        .entity(Map.of("errors", errors))
//                        .build();
//            }
//
//            return Response.status(Response.Status.NOT_FOUND)
//                    .type(MediaType.APPLICATION_JSON)
//                    .entity(Map.of("error", message))
//                    .build();
//        }
//
//        if (root instanceof JsonSyntaxException ||  exception instanceof JsonSyntaxException) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .type(MediaType.APPLICATION_JSON)
//                    .entity(Map.of("error", "Invalid JSON format"))
//                    .build();
//        }
//
//        if (root.getMessage() != null &&
//                root.getMessage().toLowerCase().contains("uuid")) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .type(MediaType.APPLICATION_JSON)
//                    .entity(Map.of("error", "Invalid UUID format"))
//                    .build();
//        }
//
//        System.err.println("Unexpected error occurred:");
//        root.printStackTrace();
//
//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                .type(MediaType.APPLICATION_JSON)
//                .entity(Map.of("error", "An unexpected error occurred"))
//                .build();
//    }
//    private Throwable unwrap(Throwable ex) {
//        Throwable cause = ex;
//        while (true) {
//            if (cause instanceof ServletException se && se.getRootCause() != null) {
//                cause = se.getRootCause();
//            } else if (cause.getCause() != null && cause.getCause() != cause) {
//                cause = cause.getCause();
//            } else {
//                break;
//            }
//        }
//        return cause;
//    }
//}


package common;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
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
@Priority(1)
@Singleton
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    public GlobalExceptionMapper() {
        System.out.println("GlobalExceptionMapper initialized successfully");
    }

    @Override
    public Response toResponse(Throwable exception) {

        System.err.println("GlobalExceptionMapper triggered: " + exception.getClass().getSimpleName() + " - " + exception.getMessage());

        Throwable root = unwrap(exception);

        if (root instanceof ServletException || exception instanceof ServletException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Servlet exception: " + root.getMessage()))
                    .build();
        }

        if (root instanceof IllegalArgumentException || exception instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) root);
        }

        if (root instanceof JsonSyntaxException || exception instanceof JsonSyntaxException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Invalid JSON format"))
                    .build();
        }

        if (root.getMessage() != null &&
                root.getMessage().toLowerCase().contains("uuid")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Invalid UUID format"))
                    .build();
        }

        System.err.println("Unexpected error occurred:");
        root.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", "An unexpected error occurred"))
                .build();
    }

    private Response handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = exception.getMessage();

        if (message != null && message.startsWith("Validation failed:")) {
            String errorList = message.replace("Validation failed: ", "");
            List<String> errors = Arrays.asList(errorList.split(", "));

            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("errors", errors))
                    .build();
        }

        int status = Response.Status.BAD_REQUEST.getStatusCode();
        if (message != null && (message.contains("not found") || message.contains("does not exist"))) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", message != null ? message : "Invalid argument"))
                .build();
    }

    private Throwable unwrap(Throwable ex) {
        Throwable cause = ex;
        int depth = 0;

        while (cause != null && depth < 10) { // Prevent infinite loops
            if (cause instanceof ServletException se && se.getRootCause() != null) {
                cause = se.getRootCause();
            } else if (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            } else {
                break;
            }
            depth++;
        }

        return cause != null ? cause : ex;
    }
}
