package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;
import com.jenjinstudios.io.concurrency.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Used for making connections so that Message objects can be read, written, and executed in a non-blocking fashion.
 *
 * @author Caleb Brinkman
 */
public class Connection
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);
    private static final int TERMINATION_TIMEOUT = 120;
    private final MessageQueue messageQueue;
    private final ScheduledExecutorService executor;
    private final ExecutionContext context;
    private final MessageReader messageReader;
    private final MessageWriter messageWriter;

    /**
     * Construct a new connection.
     *
     * @param context The context in which messages should execute.
     * @param messageReader The stream from which messages should be read.
     * @param messageWriter The stream to which messages should be written.
     */
    public Connection(
          ExecutionContext context,
          MessageReader messageReader,
          MessageWriter messageWriter)
    {
        executor = Executors.newScheduledThreadPool(4);
        this.context = context;
        this.messageReader = messageReader;
        this.messageWriter = messageWriter;
        messageQueue = new MessageQueue();
    }

    /**
     * Start sending, receiving, and executing messages.
     */
    public void start() {
        Runnable executionTask = new ExecutionTask(messageQueue, context);
        Runnable writeTask = new WriteTask(messageQueue, messageWriter);
        Runnable readTask = new ReadTask(messageQueue, messageReader);
        Runnable errorTask = new ErrorTask(messageQueue);

        executor.scheduleWithFixedDelay(errorTask, 0, 10, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(readTask, 0, 10, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(writeTask, 0, 10, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(executionTask, 0, 10, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop sending, receiving, and executing messages, and close input and output streams.
     */
    public void stop() {
        LOGGER.debug("Shutting down executor service");
        executor.shutdownNow();
        try {
            executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Exception while awaiting executor shutdown", e);
        }
        try {
            messageReader.close();
        } catch (IOException e) {
            LOGGER.warn("Exception when closing input stream", e);
        }
        try {
            messageWriter.close();
        } catch (IOException e) {
            LOGGER.warn("Exception when closing output stream", e);
        }
    }

    /**
     * Send the specified Message from this connection.  Note that this operation is not atomic; the message is added
     * to and outgoing queue, and will be sent when the thread responsible for writing outgoing messages is able to
     * process it.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(Message message) { messageQueue.queueOutgoing(message); }
}
