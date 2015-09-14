package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageWriter;
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
        MessageWriter messageWriter = mock(MessageWriter.class);
        when(messageQueue.getOutgoingAndClear())
              .thenReturn(Collections.singletonList(message))
              .thenReturn(mock(List.class));

        new WriteTask(messageQueue, messageWriter).run();

        verify(messageWriter).write(message);
    }
}