//package common;
//
//import com.google.gson.Gson;
//
//public abstract class AbstractResource {
//
//    protected Gson gson() {
//        return new com.google.gson.GsonBuilder()
//                .registerTypeAdapter(java.time.LocalDateTime.class,
//                        new com.google.gson.JsonSerializer<java.time.LocalDateTime>() {
//                            public com.google.gson.JsonElement serialize(java.time.LocalDateTime src,
//                                                                         java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
//                                return new com.google.gson.JsonPrimitive(src.toString());
//                            }
//                        })
//                .create();
//    }
//}
//
//
//
////package common;
////
////import com.google.gson.Gson;
////import jakarta.ws.rs.core.Context;
////import jakarta.ws.rs.ext.ContextResolver;
////import jakarta.ws.rs.ext.Providers;
////
////public abstract class AbstractResource {
////
////    @Context
////    private Providers providers;
////
////    protected Gson gson() {
////        ContextResolver<Gson> resolver = providers.getContextResolver(Gson.class, null);
////        return resolver.getContext(Gson.class); // always uses the provider's Gson
////    }
////}
//

package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractResource {
    private Gson gson = null; //Library for parsing json payloads to java POJOS and vice versa

    public Gson gson() {
        if (gson == null) { //singleton pattern
            gson = new GsonBuilder().create();
        }
        return gson;
    }
}
