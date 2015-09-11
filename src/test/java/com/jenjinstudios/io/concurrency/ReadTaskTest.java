package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.stream.MessageInputStream;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Used for testing the ReadTask class.
 *
 * @author Caleb Brinkman
 */
public class ReadTaskTest
{
    /**
     * Test the task execution.
     * @throws Exception If there's an exception during testing.
     */
    @Test
    public void testRun() throws Exception {
        Message message = mock(Message.class);
        MessageQueue messageQueue = mock(MessageQueue.class);
        MessageInputStream messageInputStream = mock(MessageInputStream.class);
        when(messageInputStream.read()).thenReturn(message);

        new ReadTask(messageQueue, messageInputStream).run();

        verify(messageQueue).messageReceived(message);
    }

    /**
     * Test task execution with the input stream throwing an exception.
     * @throws Exception If there is an exception thrown during testing.
     */
    @Test
    public void testRunWithException() throws Exception {
        MessageQueue messageQueue = mock(MessageQueue.class);
        MessageInputStream messageInputStream = mock(MessageInputStream.class);
        IOException ex = mock(IOException.class);
        when(messageInputStream.read()).thenThrow(ex);

        new ReadTask(messageQueue, messageInputStream).run();

        verify(messageQueue).errorEncountered(ex);
    }
}