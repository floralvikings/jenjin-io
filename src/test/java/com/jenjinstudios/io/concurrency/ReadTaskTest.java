package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Used for testing the ReadTask class.
 *
 * @author Caleb Brinkman
 */
public class ReadTaskTest
{
    /**
     * Test the task execution.
     *
     * @throws Exception If there's an exception during testing.
     */
    @Test
    public void testRun() throws Exception {
        Message message = mock(Message.class);
        MessageQueue messageQueue = mock(MessageQueue.class);
        MessageReader messageReader = mock(MessageReader.class);
        when(messageReader.read()).thenReturn(message);

        new ReadTask(messageQueue, messageReader).run();

        verify(messageQueue).messageReceived(message);
    }

    /**
     * Test task execution with the input stream throwing an exception.
     *
     * @throws Exception If there is an exception thrown during testing.
     */
    @Test
    public void testRunWithException() throws Exception {
        MessageQueue messageQueue = mock(MessageQueue.class);
        MessageReader messageReader = mock(MessageReader.class);
        IOException ex = mock(IOException.class);
        when(messageReader.read()).thenThrow(ex);

        new ReadTask(messageQueue, messageReader).run();

        verify(messageQueue).errorEncountered(ex);
    }
}