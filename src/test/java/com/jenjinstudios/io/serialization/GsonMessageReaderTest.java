package com.jenjinstudios.io.serialization;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the GsonMessageReader class.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageReaderTest
{
    /**
     * Test the read method.
     * @throws Exception If there is an exception during the test.
     */
    @Test
    public void testRead() throws Exception {
        final String json = "{\"class\":\"com.jenjinstudios.io.serialization.TestMessage\",\"fields\":{\"name\":\"foo\"}}";
        final byte[] jsonBytes = json.getBytes();
        final byte[] lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array();
        byte[] bytes = new byte[(jsonBytes.length + lengthBytes.length)];

        System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
        System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length);

        final InputStream inputStream = new ByteArrayInputStream(bytes);
        final MessageReader gsonMessageReader = new GsonMessageReader(inputStream);
        final Message message = gsonMessageReader.read();

        assertTrue(message instanceof TestMessage, "Message should be instance of TestMessage");
        assertEquals(((TestMessage)message).getName(), "foo", "Message name should be \"foo\"");
    }
}