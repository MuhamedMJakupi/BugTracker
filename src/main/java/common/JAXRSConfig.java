package common;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JAXRSConfig extends ResourceConfig {
    public JAXRSConfig() {
        packages("resource");
        register(GsonProvider.class); // Register your custom Gson provider

        // Force refresh of providers and resources
        property("jersey.config.server.provider.scanning.recursive", true);
    }
}

//@ApplicationPath("/api")
//public class JAXRSConfig extends Application {
//
//}
