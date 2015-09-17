package com.jenjinstudios.io.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks for errors, and raises a flag if necessary.
 *
 * @author Caleb Brinkman
 */
public class ErrorTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTask.class);
    private final MessageQueue messageQueue;

    /**
     * Construct a new ErrorTask that will utilize the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     */
    public ErrorTask(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        messageQueue.getErrorsAndClear().forEach(t -> LOGGER.warn("Encountered error from MessageQueue", t));
    }
}
