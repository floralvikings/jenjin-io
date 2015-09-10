package com.jenjinstudios.io.stream;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.*;

/**
 * Used to test the GsonMessageOutputStream class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageOutputStreamTest
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
        MessageOutputStream gsonMessageOutputStream = new GsonMessageOutputStream(outputStream);
        gsonMessageOutputStream.write(message);

        final String json = "{\"class\":\"com.jenjinstudios.io.stream" +
              ".GsonMessageOutputStreamTest$TestMessage\",\"fields\":{\"name\":\"foo\"}}";
        final byte[] jsonBytes = json.getBytes();
        final byte[] lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array();
        byte[] bytes = new byte[(jsonBytes.length + lengthBytes.length)];

        System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
        System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length);

        assertEquals(outputStream.toByteArray(), bytes, "Arrays should be equal.");
    }

    private static class TestMessage extends Message
    {
        private String name;

        @Override
        public Message execute(ExecutionContext context) {
            return null;
        }

        public void setName(String name) { this.name = name; }
    }
}