package com.jenjinstudios.io.serialization;

import com.google.gson.*;
import com.jenjinstudios.io.Message;

import java.lang.reflect.Type;

/**
 * Used to serialize Messages with Gson.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageSerializer implements JsonSerializer<Message>
{
    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement fields = context.serialize(src, src.getClass());
        JsonObject message = new JsonObject();
        message.addProperty("class", src.getClass().getName());
        message.add("fields", fields);
        return message;
    }
}
