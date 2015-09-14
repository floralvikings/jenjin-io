package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageWriter;

import java.io.IOException;
import java.util.List;

/**
 * Writes messages from the outgoing list in the MessageQueue.
 *
 * @author Caleb Brinkman
 */
public class WriteTask implements Runnable
{
    private final MessageQueue messageQueue;
    private final MessageWriter messageWriter;

    /**
     * Construct a new WriteTask that will send messages from the given MessageQueue via the given MessageWriter.
     *
     * @param messageQueue The MessageQueue.
     * @param messageWriter The MessageWriter.
     */
    public WriteTask(MessageQueue messageQueue, MessageWriter messageWriter) {
        this.messageQueue = messageQueue;
        this.messageWriter = messageWriter;
    }

    @Override
    public void run() {
        final List<Message> outgoing = messageQueue.getOutgoingAndClear();
        outgoing.forEach(message -> {
            try {
                messageWriter.write(message);
            } catch (IOException e) {
                messageQueue.errorEncountered(e);
            }
        });
    }
}
