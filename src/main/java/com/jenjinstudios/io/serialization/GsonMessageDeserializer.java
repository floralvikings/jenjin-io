package com.jenjinstudios.io.serialization;

import com.google.gson.*;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.annotations.MessageAdapter;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used to deserialize Message objects with Gson.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageDeserializer implements JsonDeserializer<Message>
{
    private static final Map<String, Class> MESSAGE_CLASSES = new HashMap<>(10);

    static {
        Reflections reflections = new Reflections("");
        final Set<Class<? extends Message>> messageClasses = reflections.getSubTypesOf(Message.class);
        for (Class messageClass : messageClasses) {
            Annotation annotation = messageClass.getAnnotation(MessageAdapter.class);
            if (annotation != null) {
                String adaptFromClass = ((MessageAdapter) annotation).adaptFrom().getName();
                MESSAGE_CLASSES.put(adaptFromClass, messageClass);
            } else {
                MESSAGE_CLASSES.put(messageClass.getName(), messageClass);
            }
        }
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement classElement = jsonObject.get("class");
        if (classElement == null) {
            throw new JsonParseException("Message must specify class to be deserialized into");
        }
        String className = classElement.getAsString();
        Class<Message> lookupClass = MESSAGE_CLASSES.get(className);
        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
