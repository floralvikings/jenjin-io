package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.MessageIOFactory;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;
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
public class ConnectionBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionBuilder.class);
    private final Collection<Consumer<ExecutionContext>> contextualTasks = new LinkedList<>();
    private MessageIOFactory messageIOFactory;
    private MessageReader messageReader;
    private MessageWriter messageWriter;
    private ExecutionContext executionContext;
    private BiConsumer<Connection, Throwable> errorCallback;

    /**
     * Build a connection using all the values supplied to this builder.
     *
     * @return A connection built with all the values supplied to this builder.
     */
    public Connection build() {
        if (messageReader == null) { throw new IllegalStateException("MessageReader not set"); }
        if (messageWriter == null) { throw new IllegalStateException("MessageWriter not set"); }
        if (executionContext == null) { throw new IllegalStateException("Execution Context not set"); }

        return new Connection(executionContext, messageReader, messageWriter, errorCallback, contextualTasks);
    }

    /**
     * Construct a new ConnectionBuilder that will establish a connection over the given socket.
     *
     * @param socket The socket over which the connection will be made.
     *
     * @throws IOException If there is an exception when creating streams from the given socket.
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder withSocket(Socket socket) throws IOException {
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
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder withMessageIOFactory(MessageIOFactory factory) {
        if (messageIOFactory == null) {
            if ((messageReader != null) || (messageWriter != null)) {
                LOGGER.warn("Applying MessageIOFactory after one or both streams have already been set");
            }
            this.messageIOFactory = factory;
        } else {
            throw new IllegalStateException("MessageIOFactory already set");
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
    public ConnectionBuilder withInputStream(InputStream inputStream) {
        if (messageIOFactory == null) {
            throw new IllegalStateException("MessageIOFactory not set");
        }
        if (messageReader == null) {
            messageReader = messageIOFactory.createReader(inputStream);
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
    public ConnectionBuilder withOutputStream(OutputStream outputStream) {
        if (messageIOFactory == null) {
            throw new IllegalStateException("MessageIOFactory not set");
        }
        if (messageWriter == null) {
            messageWriter = messageIOFactory.createWriter(outputStream);
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
    public ConnectionBuilder withMessageReader(MessageReader reader) {
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
     * @return This ConnectionBuilder
     */
    public ConnectionBuilder withMessageWriter(MessageWriter writer) {
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
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder withExecutionContext(ExecutionContext context) {
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
    public ConnectionBuilder withErrorCallback(BiConsumer<Connection, Throwable> callback) {
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
    public ConnectionBuilder withContextualTask(Consumer<ExecutionContext> task) {
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
    public final ConnectionBuilder withContextualTasks(Consumer<ExecutionContext>... tasks) {
        for (Consumer<ExecutionContext> task : tasks) {
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
    public ConnectionBuilder withContextualTasks(Collection<Consumer<ExecutionContext>> tasks) {
        for (Consumer<ExecutionContext> task : tasks) {
            withContextualTask(task);
        }
        return this;
    }
}
