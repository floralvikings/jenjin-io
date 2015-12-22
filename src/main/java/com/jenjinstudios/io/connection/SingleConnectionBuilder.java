package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to configure and create a Connection.
 *
 * @author Caleb Brinkman
 */
public class SingleConnectionBuilder<T extends ExecutionContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleConnectionBuilder.class);
    private final Collection<Consumer<T>> contextualTasks = new LinkedList<>();
    private final Collection<Consumer<T>> recurringTasks = new LinkedList<>();
    private final Collection<Consumer<Connection<T>>> shutdownCallbacks = new LinkedList<>();
    private MessageReaderFactory messageReaderFactory;
    private MessageWriterFactory messageWriterFactory;
    private MessageReader messageReader;
    private MessageWriter messageWriter;
    private T executionContext;
    private BiConsumer<Connection<T>, Throwable> errorCallback;

    /**
     * Build a connection using all the values supplied to this builder.
     *
     * @return A connection built with all the values supplied to this builder.
     */
    public Connection<T> build() {
        if (messageReader == null) { throw new IllegalStateException("MessageReader not set"); }
        if (messageWriter == null) { throw new IllegalStateException("MessageWriter not set"); }
        if (executionContext == null) { throw new IllegalStateException("Execution Context not set"); }

        return new Connection(executionContext,
              messageReader,
              messageWriter,
              errorCallback,
              contextualTasks,
              shutdownCallbacks,
              recurringTasks);
    }

    /**
     * Construct a new ConnectionBuilder that will establish a connection over the given socket.
     *
     * @param socket The socket over which the connection will be made.
     *
     * @return This ConnectionBuilder.
     *
     * @throws IOException If there is an exception when creating streams from the given socket.
     */
    public SingleConnectionBuilder<T> withSocket(Socket socket) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();
        withInputStream(inputStream);
        withOutputStream(outputStream);
        return this;
    }

    /**
     * Use the given MessageIOFactory to create MessageReader and MessageWriter instances from Java Input and Output
     * streams.
     *
     * @param factory The MessageIOFactory.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withMessageIOFactory(MessageIOFactory factory) {
        if ((messageReaderFactory == null) && (messageWriterFactory == null)) {
            if ((messageReader != null) || (messageWriter != null)) {
                LOGGER.warn("Applying MessageIOFactory after one or both streams have already been set");
            }
            this.messageReaderFactory = factory;
            this.messageWriterFactory = factory;
        } else {
            if (messageReaderFactory != null) {
                throw new IllegalStateException("MessageReaderFactory already set: " + messageReaderFactory);
            }
            throw new IllegalStateException("MessageWriterFactory already set" + messageWriterFactory);
        }
        return this;
    }

    /**
     * Build a connection with the given InputStream.
     *
     * @param inputStream The stream the Connection will use to read messages.
     *
     * @return This ConnectionBuilder
     */
    public SingleConnectionBuilder<T> withInputStream(InputStream inputStream) {
        if (messageReaderFactory == null) {
            throw new IllegalStateException("MessageReaderFactory not set");
        }
        if (messageReader == null) {
            messageReader = messageReaderFactory.createReader(inputStream);
        } else {
            throw new IllegalStateException("MessageReader is already set");
        }
        return this;
    }

    /**
     * Build a connection with the given OutputStream.
     *
     * @param outputStream The output stream the Connection will use to write messages.
     *
     * @return This ConnectionBuilder
     */
    public SingleConnectionBuilder<T> withOutputStream(OutputStream outputStream) {
        if (messageWriterFactory == null) {
            throw new IllegalStateException("MessageIOFactory not set");
        }
        if (messageWriter == null) {
            messageWriter = messageWriterFactory.createWriter(outputStream);
        } else {
            throw new IllegalStateException("MessageWriter is already set");
        }
        return this;
    }

    /**
     * Build a connection with the given InputStream.
     *
     * @param reader The stream the Connection will use to read messages.
     *
     * @return This ConnectionBuilder
     */
    public SingleConnectionBuilder<T> withMessageReader(MessageReader reader) {
        if (messageReader == null) {
            messageReader = reader;
        } else {
            throw new IllegalStateException("MessageReader is already set");
        }
        return this;
    }

    /**
     * Build a connection with the given OutputStream.
     *
     * @param writer The output stream the Connection will use to write messages.
     *
     * @return This ConnectionBuilder
     */
    public SingleConnectionBuilder<T> withMessageWriter(MessageWriter writer) {
        if (messageWriter == null) {
            messageWriter = writer;
        } else {
            throw new IllegalStateException("MessageWriter is already set");
        }
        return this;
    }

    /**
     * Build a connection with the given ExecutionContext.
     *
     * @param context The context in which the Connection will execute messages.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withExecutionContext(T context) {
        if (executionContext == null) {
            executionContext = context;
        } else {
            throw new IllegalStateException("Execution context is already set");
        }
        return this;
    }

    /**
     * Build a connection with the given error callback function.
     *
     * @param callback The Consumer (accepting a Connection and Throwable) that will be invoked when an error is
     * encountered.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withErrorCallback(BiConsumer<Connection<T>, Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param task The task to be executed; a Consumer accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withContextualTask(Consumer<T> task) {
        contextualTasks.add(task);
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The tasks to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    @SafeVarargs
    public final SingleConnectionBuilder<T> withContextualTasks(Consumer<T>... tasks) {
        for (Consumer<T> task : tasks) {
            withContextualTask(task);
        }
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The tasks to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withContextualTasks(Iterable<Consumer<T>> tasks) {
        tasks.forEach(this::withContextualTask);
        return this;
    }

    /**
     * Build a connection that will invoke the given consumers when shutting down with itself as a parameter.
     *
     * @param callbacks The consumers.
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withShutdownCallbacks(Iterable<Consumer<Connection<T>>> callbacks) {
        callbacks.forEach(this::withShutdownCallback);
        return this;
    }

    /**
     * Build a connection that will invoke the given consumer when shutting down with itself as a parameter.
     *
     * @param callback The consumer
     *
     * @return This ConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withShutdownCallback(Consumer<Connection<T>> callback) {
        shutdownCallbacks.add(callback);
        return this;
    }

    /**
     * Use the specified MessageReaderFactory to build a MessageReader from any InputStreams.
     *
     * @param factory The MessageReaderFactory.
     *
     * @return This SingleConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withMessageReaderFactory(MessageReaderFactory factory) {
        if (this.messageReaderFactory == null) {
            if (messageReader != null) {
                LOGGER.warn("Applying MessageReaderFactory after stream has already been set: " + messageReader);
            }
            this.messageReaderFactory = factory;
        } else {
            throw new IllegalStateException("MessageReaderFactory already set");
        }
        return this;
    }

    /**
     * Use the specified MessageWriterFactory to build a MessageWriter from any OutputStreams.
     *
     * @param factory The MessageWriterFactory.
     *
     * @return This SingleConnectionBuilder.
     */
    public SingleConnectionBuilder<T> withMessageWriterFactory(MessageWriterFactory factory) {
        if (this.messageWriterFactory == null) {
            if (messageWriter != null) {
                LOGGER.warn("Applying MessageWriterFactory after stream has already been set: " + messageWriter);
            }
            this.messageWriterFactory = factory;
        } else {
            throw new IllegalStateException("MessageWriterFactory already set");
        }
        return this;
    }
}
