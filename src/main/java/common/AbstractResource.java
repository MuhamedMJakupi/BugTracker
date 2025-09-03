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
