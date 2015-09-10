package com.jenjinstudios.io.stream;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test the GsonMessageInputStream class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageInputStreamTest
{
    /**
     * Test the read method.
     * @throws Exception If there is an exception during the test.
     */
    @Test
    public void testRead() throws Exception {
        final String json = "{\"class\":\"com.jenjinstudios.io.stream" +
              ".GsonMessageInputStreamTest$TestMessage\",\"fields\":{\"name\":\"foo\"}}";
        final byte[] jsonBytes = json.getBytes();
        final byte[] lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array();
        byte[] bytes = new byte[(jsonBytes.length + lengthBytes.length)];

        System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
        System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length);

        final InputStream inputStream = new ByteArrayInputStream(bytes);
        final MessageInputStream gsonMessageInputStream = new GsonMessageInputStream(inputStream);
        final Message message = gsonMessageInputStream.read();

        assertTrue(message instanceof TestMessage, "Message should be instance of TestMessage");
        assertEquals(((TestMessage)message).getName(), "foo", "Message name should be \"foo\"");
    }

    private static class TestMessage extends Message
    {
        private String name;

        @Override
        public Message execute(ExecutionContext context) {
            return null;
        }

        public String getName() { return name; }
    }
}