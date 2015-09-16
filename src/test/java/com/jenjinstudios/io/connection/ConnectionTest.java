package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;


/**
 * Used to test the Connection class.
 *
 * @author Caleb Brinkman
 */
public class ConnectionTest
{
    /**
     * Test starting a connection, receiving a message, writing a response, and shutting down.
     *
     * @throws Exception If there's an exception during testing.
     */
    @Test
    public void testRun() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        MessageReader reader = mock(MessageReader.class);
        MessageWriter writer = mock(MessageWriter.class);
        Message incoming = mock(Message.class);
        Message outgoing = mock(Message.class);

        when(reader.read()).thenReturn(incoming).thenAnswer(invocation -> {
            Thread.sleep(1000); // Forces read method to hang
            return null;
        });

        when(incoming.execute(context)).thenReturn(outgoing);

        Connection connection = new Connection(context, reader, writer);
        connection.start();

        // Allow connection time to process message
        Thread.sleep(100);

        verify(reader, atLeast(1)).read();
        verify(writer).write(outgoing);

        connection.stop();
    }

}