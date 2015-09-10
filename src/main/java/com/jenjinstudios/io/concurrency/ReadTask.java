package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.stream.MessageInputStream;

import java.io.IOException;

/**
 * Reads incoming messages and stores them in a MessageQueue.
 *
 * @author Caleb Brinkman
 */
public class ReadTask implements Runnable
{
    private final MessageQueue messageQueue;
    private final MessageInputStream inputStream;

    /**
     * Construct a new ReadTask that will read from the given message input stream and store the incoming messages in
     * the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param inputStream The MessageInputStream.
     */
    public ReadTask(MessageQueue messageQueue, MessageInputStream inputStream) {
        this.messageQueue = messageQueue;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            final Message message = inputStream.read();
            messageQueue.messageReceived(message);
        } catch (IOException e) {
            messageQueue.errorEncountered(e);
        }
    }
}
