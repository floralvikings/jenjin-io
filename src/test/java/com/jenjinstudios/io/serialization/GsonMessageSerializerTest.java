package com.jenjinstudios.io.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.io.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the GsonMessageSerializer class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageSerializerTest
{
    /**
     * Test the serialization.
     *
     * @throws Exception If there is an exception during test execution.
     */
    @Test
    public void testSerialize() throws Exception {

        final TestMessage testMessage = new TestMessage();
        testMessage.setName("foo");

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class, new GsonMessageSerializer());
        final Gson gson = gsonBuilder.create();

        final String json = gson.toJson(testMessage, Message.class);
        final String expectedJson = "{\"class\":\"com.jenjinstudios.io.serialization.TestMessage\"," +
              "\"fields\":{\"name\":\"foo\"}}";
        Assert.assertEquals(json, expectedJson, "Serialized form should match.");
    }
}