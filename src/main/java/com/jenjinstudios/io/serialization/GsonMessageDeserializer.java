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
    private final Map<String, Class> annotatedMessages;

    /**
     * Construct a new GsonMessageDeserializer.
     */
    public GsonMessageDeserializer() {
        annotatedMessages = new HashMap<>(10);

        Reflections reflections = new Reflections("");
        final Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(MessageAdapter.class);
        for (Class annotatedClass : annotated) {
            Annotation annotation = annotatedClass.getAnnotation(MessageAdapter.class);
            String messageClass = ((MessageAdapter) annotation).adaptFrom().getName();
            annotatedMessages.put(messageClass, annotatedClass);
        }
    }

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
        if (annotatedMessages.containsKey(className)) {
            lookupClass = annotatedMessages.get(className);
        } else {
            try {
                lookupClass = (Class<Message>) Class.forName(className);
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new JsonParseException("Message implementation not found or incorrect: ", e);
            }
        }
        JsonElement fieldsElement = jsonObject.get("fields");
        return (fieldsElement != null)
              ? context.deserialize(fieldsElement, lookupClass)
              : context.deserialize(new JsonObject(), lookupClass);
    }
}
