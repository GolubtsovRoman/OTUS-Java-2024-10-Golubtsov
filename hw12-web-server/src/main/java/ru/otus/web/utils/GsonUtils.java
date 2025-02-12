package ru.otus.web.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.otus.jpql.crm.model.entity.Phone;

import java.lang.reflect.Type;

public class GsonUtils {

    public static Gson getGson() {
        var gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting();

        // я бы это делал jackson аннотациями, но в ДЗ интересно попробовать Gson
        var jsonSerializer = new JsonSerializer<Phone>() {
            @Override
            public JsonElement serialize(Phone src, Type typeOfSrc, JsonSerializationContext context) {
                var jsonObject = new JsonObject();
                jsonObject.addProperty("id", src.getId());
                jsonObject.addProperty("number", src.getNumber());
                if (src.getClient() != null) {
                    jsonObject.addProperty("clientId", src.getClient().getId());
                }
                return jsonObject;
            }
        };
        gsonBuilder.registerTypeAdapter(Phone.class, jsonSerializer);

        return gsonBuilder.create();
    }

}
