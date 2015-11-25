package com.jenjinstudios.io.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
 * Used to serialize Messages with Gson.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageSerializer implements JsonSerializer<Message>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GsonMessageSerializer.class);
    private static final Map<Class, String> ADAPT_TO = new HashMap<>(10);

    static {
        LOGGER.debug("Scanning for message classes");
        Reflections reflections = new Reflections("");
        final Set<Class<? extends Message>> messageClasses = reflections.getSubTypesOf(Message.class);
        for (Class messageClass : messageClasses) {
            LOGGER.debug("Registering message class: " + messageClass.getName());
            Annotation annotation = messageClass.getAnnotation(MessageAdapter.class);
            if (annotation != null) {
                final MessageAdapter messageAdapter = (MessageAdapter) annotation;
                String adaptTo = messageAdapter.adaptTo();
                if (!adaptTo.isEmpty()) {
                    LOGGER.debug("Registering adapter class: " + adaptTo);
                    try {
                        Class.forName(adaptTo);
                        ADAPT_TO.put(messageClass, adaptTo);
                    } catch (ClassNotFoundException e) {
                        LOGGER.warn("Encountered ClassNotFoundException when registering message adapter", e);
                    }
                }
            }
        }
        LOGGER.debug("Registered \"adaptTo\" Message Classes: {}", ADAPT_TO);
    }

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        final Class<? extends Message> srcClass = src.getClass();
        JsonElement fields = context.serialize(src, srcClass);
        JsonObject message = new JsonObject();

        String className = ADAPT_TO.containsKey(srcClass) ? ADAPT_TO.get(srcClass) : srcClass.getName();

        message.addProperty("class", className);
        message.add("fields", fields);
        return message;
    }
}
