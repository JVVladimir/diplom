package ru.hse.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonManager {

    public static GsonBuilder builder = new GsonBuilder();

    public static String entityToJson(JsonEntity entity) {
        Gson gson = builder.create();
        return gson.toJson(entity);
    }

    public static JsonEntity jsonToEntity(String jsonText) {
        Gson gson = builder.create();
        return gson.fromJson(jsonText, JsonEntity.class);
    }

}
