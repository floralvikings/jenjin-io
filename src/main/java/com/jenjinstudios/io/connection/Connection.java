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
import java.util.function.BiConsumer;

/**
 * Used for making connections so that Message objects can be read, written, and executed in a non-blocking fashion.
 *
 * @author Caleb Brinkman
 */
public class Connection<C extends ExecutionContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);
    private final BiConsumer<Connection, Throwable> errorCallback;
    private final MessageQueue messageQueue;
    private final ScheduledExecutorService executor;
    private final C context;
    private final MessageReader messageReader;
    private final MessageWriter messageWriter;

    /**
     * Construct a new connection.
     *
     * @param context The context in which messages should execute.
     * @param messageReader The stream from which messages should be read.
     * @param messageWriter The stream to which messages should be written.
     */
    Connection(C context, MessageReader messageReader, MessageWriter messageWriter)
    {
        this(context, messageReader, messageWriter, null);
    }

    /**
     * Construct a new connection.
     *
     * @param context The context in which messages should execute.
     * @param messageReader The stream from which messages should be read.
     * @param messageWriter The stream to which messages should be written.
     */
    Connection(C context, MessageReader messageReader, MessageWriter messageWriter, BiConsumer<Connection, Throwable>
          errorCallback)
    {
        executor = Executors.newScheduledThreadPool(4);
        this.context = context;
        this.messageReader = messageReader;
        this.messageWriter = messageWriter;
        messageQueue = new MessageQueue();
        this.errorCallback = errorCallback;
    }

    /**
     * Start sending, receiving, and executing messages.
     */
    public void start() {
        Runnable executionTask = new ExecutionTask(messageQueue, context);
        Runnable writeTask = new WriteTask(messageQueue, messageWriter);
        Runnable readTask = new ReadTask(messageQueue, messageReader);
        Runnable errorTask = new ErrorTask(messageQueue, this::errorEncountered);

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
     * This method is an "emergency stop" in the event that a fatal error is encountered by the error checking thread.
     * <p>
     * Without supplying a custom error callback, this is the default callback for <i>all</i> errors encountered.
     *
     * @param cause The exception which triggered this shutdown.
     */
    public final void errorEncountered(Throwable cause) {
        if (errorCallback != null) {
            errorCallback.accept(this, cause);
        }
    }

    public C getContext() { return context; }

    /**
     * Send the specified Message from this connection.  Note that this operation is not atomic; the message is added
     * to and outgoing queue, and will be sent when the thread responsible for writing outgoing messages is able to
     * process it.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(Message message) { messageQueue.queueOutgoing(message); }
}
