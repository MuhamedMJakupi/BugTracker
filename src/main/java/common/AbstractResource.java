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

//package common;
//
//import com.google.gson.Gson;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.ext.ContextResolver;
//import jakarta.ws.rs.ext.Providers;
//
//    public abstract class AbstractResource {
//
//        @Context
//        private Providers providers;
//
//        // Get the configured Gson from your GsonProvider
//        protected Gson gson() {
//            ContextResolver<Gson> resolver = providers.getContextResolver(Gson.class, null);
//            return resolver != null ? resolver.getContext(null) : new Gson();
//        }
//    }

//package common;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.TypeAdapter;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonWriter;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public abstract class AbstractResource {
//
//    // Use the same configuration as your GsonProvider
//    private static final Gson GSON = new GsonBuilder()
//            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
//            .create();
//
//    protected Gson gson() {
//        return GSON;
//    }
//
//    // Same adapters as your GsonProvider
//    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//
//        @Override
//        public void write(JsonWriter out, LocalDateTime value) throws IOException {
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(value.format(formatter));
//            }
//        }
//
//        @Override
//        public LocalDateTime read(JsonReader in) throws IOException {
//            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
//                in.nextNull();
//                return null;
//            } else {
//                return LocalDateTime.parse(in.nextString(), formatter);
//            }
//        }
//    }
//
//    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//        @Override
//        public void write(JsonWriter out, LocalDate value) throws IOException {
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(value.format(formatter));
//            }
//        }
//
//        @Override
//        public LocalDate read(JsonReader in) throws IOException {
//            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
//                in.nextNull();
//                return null;
//            } else {
//                return LocalDate.parse(in.nextString(), formatter);
//            }
//        }
//    }
//}


//package common;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.TypeAdapter;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonWriter;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public abstract class AbstractResource {
//
//    // Static instance to avoid creating Gson repeatedly
//    private static final Gson GSON_INSTANCE = new GsonBuilder()
//            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())  // ADDED: Missing LocalDate support
//            .create();
//
//    protected Gson gson() {
//        return GSON_INSTANCE;
//    }
//
//    // Same adapters as your GsonProvider for consistency
//    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//
//        @Override
//        public void write(JsonWriter out, LocalDateTime value) throws IOException {
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(value.format(formatter));
//            }
//        }
//
//        @Override
//        public LocalDateTime read(JsonReader in) throws IOException {
//            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
//                in.nextNull();
//                return null;
//            } else {
//                return LocalDateTime.parse(in.nextString(), formatter);
//            }
//        }
//    }
//
//    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//        @Override
//        public void write(JsonWriter out, LocalDate value) throws IOException {
//            if (value == null) {
//                out.nullValue();
//            } else {
//                out.value(value.format(formatter));
//            }
//        }
//
//        @Override
//        public LocalDate read(JsonReader in) throws IOException {
//            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
//                in.nextNull();
//                return null;
//            } else {
//                return LocalDate.parse(in.nextString(), formatter);
//            }
//        }
//    }
//}

