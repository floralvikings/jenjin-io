package com.jenjinstudios.io.concurrency;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Used for testing the ErrorTask class.
 *
 * @author Caleb Brinkman
 */
public class ErrorTaskTest
{
    /**
     * Test the task execution.
     *
     * @throws Exception If there is an exception during testing.
     */
    @Test
    public void testRun() throws Exception {
        MessageQueue messageQueue = mock(MessageQueue.class);
        Throwable throwable = mock(Throwable.class);
        Consumer<Throwable> callback = mock(Consumer.class);
        when(messageQueue.getErrorsAndClear())
              .thenReturn(Collections.singletonList(throwable))
              .thenReturn(mock(List.class));

        Runnable errorTask = new ErrorTask(messageQueue, callback);
        errorTask.run();

        verify(messageQueue, times(1)).getErrorsAndClear();
        verify(callback).accept(throwable);
    }
}