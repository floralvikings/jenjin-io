package com.jenjinstudios.io.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.io.Message;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the GsonMessageDeserializer class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageDeserializerTest
{
    /**
     * Test the deserialize method.
     * @throws Exception If there's an exception during testing.
     */
    @Test
    public void testDeserialize() throws Exception {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class, new GsonMessageDeserializer());
        final Gson gson = gsonBuilder.create();

        final String json = "{\"class\":\"com.jenjinstudios.io.serialization.TestMessage\",\"fields\":{\"name\":\"foo\"}}";
        final Message message = gson.fromJson(json, Message.class);

        assertTrue(message instanceof AdaptedMessage, "Message should be instance of AdaptedMessage");
        assertEquals(((AdaptedMessage) message).getName(), "foo", "Message should have name \"foo\"");
    }
}