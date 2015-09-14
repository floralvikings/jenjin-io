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
    private final MessageWriter outputStream;

    /**
     * Construct a new WriteTask that will send messages from the given MessageQueue via the given MessageOutputStream.
     *
     * @param messageQueue The MessageQueue.
     * @param outputStream The MessageOutputStream.
     */
    public WriteTask(MessageQueue messageQueue, MessageWriter outputStream) {
        this.messageQueue = messageQueue;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        final List<Message> outgoing = messageQueue.getOutgoingAndClear();
        outgoing.forEach(message -> {
            try {
                outputStream.write(message);
            } catch (IOException e) {
                messageQueue.errorEncountered(e);
            }
        });
    }
}
