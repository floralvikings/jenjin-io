package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * Test the MessageQueue class.
 *
 * @author Caleb Brinkman
 */
public class MessageQueueTest
{
    /**
     * Test the getIncomingAndClear method.
     *
     * @throws Exception If there is an exception during testing.
     */
    @Test
    public void testGetIncomingAndClear() throws Exception {
        Message message = mock(Message.class);

        MessageQueue messageQueue = new MessageQueue();
        messageQueue.messageReceived(message);

        final List<Message> incoming = messageQueue.getIncomingAndClear();
        assertEquals(incoming.size(), 1, "Should be one incoming message");

        final List<Message> clearIncoming = messageQueue.getIncomingAndClear();
        assertEquals(clearIncoming.size(), 0, "List should be empty");
    }

    /**
     * Test the getOutgoingAndClear method.
     *
     * @throws Exception If there is an exception during testing.
     */
    @Test
    public void testGetOutgoingAndClear() throws Exception {
        Message message = mock(Message.class);

        MessageQueue messageQueue = new MessageQueue();
        messageQueue.queueOutgoing(message);

        final List<Message> outgoing = messageQueue.getOutgoingAndClear();
        assertEquals(outgoing.size(), 1, "Should be one outgoing message");

        final List<Message> clearOutgoing = messageQueue.getOutgoingAndClear();
        assertEquals(clearOutgoing.size(), 0, "List should be empty");
    }

    /**
     * Test the getErrorsAndClear method.
     *
     * @throws Exception If there is an exception during testing.
     */
    @Test
    public void testGetErrorsAndClear() throws Exception {
        Throwable message = mock(Throwable.class);

        MessageQueue messageQueue = new MessageQueue();
        messageQueue.errorEncountered(message);

        final List<Throwable> errors = messageQueue.getErrorsAndClear();
        assertEquals(errors.size(), 1, "Should be one errors message");

        final List<Throwable> clearErrors = messageQueue.getErrorsAndClear();
        assertEquals(clearErrors.size(), 0, "List should be empty");
    }
}