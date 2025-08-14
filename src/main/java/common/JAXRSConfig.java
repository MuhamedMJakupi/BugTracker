package common;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JAXRSConfig extends ResourceConfig {
    public JAXRSConfig() {
        packages("resource");
        register(GsonProvider.class); // Register your custom Gson provider
    }
}

//@ApplicationPath("/api")
//public class JAXRSConfig extends Application {
//
//}
