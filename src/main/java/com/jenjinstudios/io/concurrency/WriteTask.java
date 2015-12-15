package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Writes messages from the outgoing list in the MessageQueue.
 *
 * @author Caleb Brinkman
 */
public class WriteTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteTask.class);
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
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Attempting to write message.  (Type: {})" + message.getClass().getName());
                }
                messageWriter.write(message);
            } catch (IOException e) {
                messageQueue.errorEncountered(e);
                try {
                    messageWriter.close();
                } catch (IOException e1) {
                    LOGGER.warn("Error when closing message writer", e1);
                }
            }
        });
    }
}
