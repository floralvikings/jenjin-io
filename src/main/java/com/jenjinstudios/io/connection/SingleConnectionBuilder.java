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
public class SingleConnectionBuilder implements ConnectionBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleConnectionBuilder.class);
    private final Collection<Consumer<ExecutionContext>> contextualTasks = new LinkedList<>();
    private final Collection<Consumer<Connection>> shutdownCallbacks = new LinkedList<>();
    private MessageIOFactory messageIOFactory;
    private MessageReader messageReader;
    private MessageWriter messageWriter;
    private ExecutionContext executionContext;
    private BiConsumer<Connection, Throwable> errorCallback;

    @Override
    public Connection build() {
        if (messageReader == null) { throw new IllegalStateException("MessageReader not set"); }
        if (messageWriter == null) { throw new IllegalStateException("MessageWriter not set"); }
        if (executionContext == null) { throw new IllegalStateException("Execution Context not set"); }

        return new Connection(executionContext, messageReader, messageWriter, errorCallback, contextualTasks,
              shutdownCallbacks);
    }

    @Override
    public SingleConnectionBuilder withSocket(Socket socket) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();
        withInputStream(inputStream);
        withOutputStream(outputStream);
        return this;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public ConnectionBuilder withMessageReader(MessageReader reader) {
        if (messageReader == null) {
            messageReader = reader;
        } else {
            throw new IllegalStateException("MessageReader is already set");
        }
        return this;
    }

    @Override
    public ConnectionBuilder withMessageWriter(MessageWriter writer) {
        if (messageWriter == null) {
            messageWriter = writer;
        } else {
            throw new IllegalStateException("MessageWriter is already set");
        }
        return this;
    }

    @Override
    public ConnectionBuilder withExecutionContext(ExecutionContext context) {
        if (executionContext == null) {
            executionContext = context;
        } else {
            throw new IllegalStateException("Execution context is already set");
        }
        return this;
    }

    @Override
    public ConnectionBuilder withErrorCallback(BiConsumer<Connection, Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }

    @Override
    public ConnectionBuilder withContextualTask(Consumer<ExecutionContext> task) {
        contextualTasks.add(task);
        return this;
    }

    @Override
    @SafeVarargs
    public final ConnectionBuilder withContextualTasks(Consumer<ExecutionContext>... tasks) {
        for (Consumer<ExecutionContext> task : tasks) {
            withContextualTask(task);
        }
        return this;
    }

    @Override
    public ConnectionBuilder withContextualTasks(Iterable<Consumer<ExecutionContext>> tasks) {
        tasks.forEach(this::withContextualTask);
        return this;
    }

    @Override
    public ConnectionBuilder withShutdownCallbacks(Iterable<Consumer<Connection>> callbacks) {
        callbacks.forEach(this::withShutdownCallback);
        return this;
    }

    @Override
    public ConnectionBuilder withShutdownCallback(Consumer<Connection> callback) {
        shutdownCallbacks.add(callback);
        return this;
    }
}
