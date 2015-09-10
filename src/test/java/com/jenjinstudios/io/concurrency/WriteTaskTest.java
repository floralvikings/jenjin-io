package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.stream.MessageOutputStream;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Used for testing the WriteTask class.
 *
 * @author Caleb Brinkman
 */
public class WriteTaskTest
{
    /**
     * Test the task execution.
     *
     * @throws Exception If there's an exception.
     */
    @Test
    public void testRun() throws Exception {
        MessageQueue messageQueue = mock(MessageQueue.class);
        Message message = mock(Message.class);
        MessageOutputStream messageOutputStream = mock(MessageOutputStream.class);
        when(messageQueue.getOutgoingAndClear())
              .thenReturn(Collections.singletonList(message))
              .thenReturn(mock(List.class));

        new WriteTask(messageQueue, messageOutputStream).run();

        verify(messageOutputStream).write(message);
    }
}