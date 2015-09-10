package com.jenjinstudios.io.serialization;

import com.google.gson.*;
import com.jenjinstudios.io.Message;

import java.lang.reflect.Type;

/**
 * Used to deserialize Message objects with Gson.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageDeserializer implements JsonDeserializer<Message>
{
    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement classElement = jsonObject.get("class");
        if (classElement == null) {
            throw new JsonParseException("Message must specify class to be deserialized into");
        }
        String className = classElement.getAsString();
        Class<Message> lookupClass;
        try {
            lookupClass = (Class<Message>) Class.forName(className);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new JsonParseException("Message implementation not found or incorrect: ", e);
        }
        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
