package com.jenjinstudios.io.serialization;

import com.google.gson.*;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.annotations.MessageAdapter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(GsonMessageDeserializer.class);
    private static final Map<String, Class> ADAPT_FROM = new HashMap<>(10);

    static {
        LOGGER.debug("Scanning for message classes");
        Reflections reflections = new Reflections("");
        final Set<Class<? extends Message>> messageClasses = reflections.getSubTypesOf(Message.class);
        for (Class messageClass : messageClasses) {
            LOGGER.debug("Registering message class: " + messageClass.getName());
            Annotation annotation = messageClass.getAnnotation(MessageAdapter.class);
            if (annotation != null) {
                final MessageAdapter messageAdapter = (MessageAdapter) annotation;
                String adaptFromClass = messageAdapter.adaptFrom();
                if (!adaptFromClass.isEmpty()) {
                    LOGGER.debug("Registering adapter class: " + adaptFromClass);
                    ADAPT_FROM.put(adaptFromClass, messageClass);
                }
            }
        }
        LOGGER.debug("Registered \"adaptFrom\" Message Classes: {}", ADAPT_FROM);
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
        Class<Message> lookupClass;
        if (ADAPT_FROM.containsKey(className)) {
            lookupClass = ADAPT_FROM.get(className);
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
