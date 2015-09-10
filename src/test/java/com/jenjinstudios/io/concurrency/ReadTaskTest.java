package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.stream.MessageInputStream;
import org.mockito.Mockito;
import org.testng.annotations.Test;

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
}