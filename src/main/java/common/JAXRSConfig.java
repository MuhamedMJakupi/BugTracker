package common;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JAXRSConfig extends ResourceConfig {
    public JAXRSConfig() {
        packages("resource","common");
        register(common.GsonProvider.class);

        // Force refresh of providers and resources
        //property("jersey.config.server.provider.scanning.recursive", true);

    }
}

//@ApplicationPath("/api")
//public class JAXRSConfig extends Application {
//
//}
