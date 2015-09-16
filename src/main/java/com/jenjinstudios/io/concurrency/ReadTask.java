package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;

import java.io.IOException;

/**
 * Reads incoming messages and stores them in a MessageQueue.
 *
 * @author Caleb Brinkman
 */
public class ReadTask implements Runnable
{
    private final MessageQueue messageQueue;
    private final MessageReader messageReader;

    /**
     * Construct a new ReadTask that will read from the given message input stream and store the incoming messages in
     * the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param messageReader The MessageReader.
     */
    public ReadTask(MessageQueue messageQueue, MessageReader messageReader) {
        this.messageQueue = messageQueue;
        this.messageReader = messageReader;
    }

    @Override
    public void run() {
        try {
            final Message message = messageReader.read();
            messageQueue.messageReceived(message);
        } catch (IOException e) {
            messageQueue.errorEncountered(e);
        }
    }
}