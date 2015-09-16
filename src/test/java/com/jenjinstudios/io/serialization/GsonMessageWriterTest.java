package com.jenjinstudios.io.serialization;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageWriter;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;

/**
 * Used to test the GsonMessageWriter class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageWriterTest
{
    /**
     * Test the write method.
     * @throws Exception If there is an exception during testing.
     */
    @Test
    public void testWrite() throws Exception {
        TestMessage message = new TestMessage();
        message.setName("foo");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageWriter gsonMessageWriter = new GsonMessageWriter(outputStream);
        gsonMessageWriter.write(message);

        final String json = "{\"class\":\"com.jenjinstudios.io.serialization" +
              ".GsonMessageWriterTest$TestMessage\",\"fields\":{\"name\":\"foo\"}}";
        final byte[] jsonBytes = json.getBytes();
        final byte[] lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array();
        byte[] bytes = new byte[(jsonBytes.length + lengthBytes.length)];

        System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
        System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length);

        assertEquals(outputStream.toByteArray(), bytes, "Arrays should be equal.");
    }

    private static class TestMessage implements Message
    {
        private String name;

        @Override
        public Message execute(ExecutionContext context) {
            return null;
        }

        public void setName(String name) { this.name = name; }
    }
}