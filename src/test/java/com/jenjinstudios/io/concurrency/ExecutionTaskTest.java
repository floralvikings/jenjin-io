package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Used for testing the ExecutionTask class.
 *
 * @author Caleb Brinkman
 */
public class ExecutionTaskTest
{
    /**
     * Test the execution of the task.
     * @throws Exception If there's an exception.
     */
    @Test
    public void testRun() throws Exception {
        MessageQueue messageQueue = mock(MessageQueue.class);
        Message message = mock(Message.class);
        ExecutionContext executionContext = mock(ExecutionContext.class);
        when(messageQueue.getIncomingAndClear())
              .thenReturn(Collections.singletonList(message))
              .thenReturn(mock(List.class));

        new ExecutionTask(messageQueue, executionContext).run();

        verify(message).execute(executionContext);
    }
}