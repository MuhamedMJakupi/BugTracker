package common;

import com.google.gson.Gson;

public abstract class AbstractResource {

    // Use a simple Gson instance for manual parsing
    protected Gson gson() {
        return new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class,
                        new com.google.gson.JsonSerializer<java.time.LocalDateTime>() {
                            public com.google.gson.JsonElement serialize(java.time.LocalDateTime src,
                                                                         java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                                return new com.google.gson.JsonPrimitive(src.toString());
                            }
                        })
                .create();
    }
}
