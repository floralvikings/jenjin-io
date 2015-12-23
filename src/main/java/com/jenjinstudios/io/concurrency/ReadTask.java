package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Reads incoming messages and stores them in a MessageQueue.
 *
 * @author Caleb Brinkman
 */
public class ReadTask<T extends ExecutionContext> implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadTask.class);
    private final MessageQueue<T> messageQueue;
    private final MessageReader messageReader;
    private volatile boolean noError = true;

    /**
     * Construct a new ReadTask that will read from the given message input stream and store the incoming messages in
     * the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param messageReader The MessageReader.
     */
    public ReadTask(MessageQueue<T> messageQueue, MessageReader messageReader) {
        this.messageQueue = messageQueue;
        this.messageReader = messageReader;
    }

    @Override
    public void run() {
        try {
            if (noError) {
                final Message message = messageReader.read();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Read Message (Type: {})", message.getClass().getName());
                }
                messageQueue.messageReceived(message);
            }
        } catch (IOException e) {
            noError = false;
            messageQueue.errorEncountered(e);
            try {
                messageReader.close();
            } catch (IOException closeException) {
                LOGGER.warn("Error when closing message reader", closeException);
            }
        }
    }
}
