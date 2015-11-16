package com.jenjinstudios.io.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.util.function.Consumer;

/**
 * Checks for errors, and raises a flag if necessary.
 *
 * @author Caleb Brinkman
 */
public class ErrorTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTask.class);
    private final MessageQueue messageQueue;
    private final Consumer<Throwable> errorCallback;

    /**
     * Construct a new ErrorTask that will utilize the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param errorCallback A Consumable to be called when an error is encountered.
     */
    public ErrorTask(MessageQueue messageQueue, Consumer<Throwable> errorCallback) {
        this.messageQueue = messageQueue;
        this.errorCallback = errorCallback;
    }

    @Override
    public void run() {
        messageQueue.getErrorsAndClear().forEach(t -> {
            if (t instanceof EOFException) {
                LOGGER.warn("Encountered EOF from MessageQueue; message: {}", t.getLocalizedMessage());
            } else {
                LOGGER.warn("Encountered error from MessageQueue", t);
            }
            errorCallback.accept(t);
        });
    }
}
