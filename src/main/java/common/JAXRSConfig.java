package common;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JAXRSConfig extends ResourceConfig {
    public JAXRSConfig() {
        packages("common","resource");
        //register(common.GsonProvider.class);

        // Force refresh of providers and resources
        //property("jersey.config.server.provider.scanning.recursive", true);

        //property("jersey.config.server.disableMoxyJson", true);
        //property("jersey.config.server.disableJsonProcessing", true);
    }
}

//@ApplicationPath("/api")
//public class JAXRSConfig extends Application {
//
//}
