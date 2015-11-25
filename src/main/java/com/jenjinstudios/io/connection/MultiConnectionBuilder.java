package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to generate multiple connections from sockets.
 *
 * @author Caleb Brinkman
 */
public class MultiConnectionBuilder<T extends ExecutionContext>
{
    private final Collection<Consumer<T>> contextualTasks = new LinkedList<>();
    private final Collection<Consumer<Connection<T>>> shutdownCallbacks = new LinkedList<>();
    private MessageReaderFactory messageReaderFactory;
    private MessageWriterFactory messageWriterFactory;
    private ExecutionContextFactory<T> executionContextFactory;
    private BiConsumer<Connection<T>, Throwable> errorCallback;

    /**
     * Build a connection using the given socket.
     *
     * @param socket The socket.
     *
     * @return The built connection.
     *
     * @throws IOException If there is an exception when building the connection.
     */
    public Connection<T> build(Socket socket) throws IOException {
        return new SingleConnectionBuilder<T>()
              .withMessageReaderFactory(messageReaderFactory)
              .withMessageWriterFactory(messageWriterFactory)
              .withErrorCallback(errorCallback)
              .withContextualTasks(contextualTasks)
              .withExecutionContext(executionContextFactory.createInstance())
              .withSocket(socket)
              .withShutdownCallbacks(shutdownCallbacks)
              .build();
    }

    /**
     * Build a connection with the given error callback function.
     *
     * @param callback The Consumer (accepting a Connection and Throwable) that will be invoked when an error is
     * encountered.
     *
     * @return This ConnectionBuilder.
     */
    public MultiConnectionBuilder<T> withErrorCallback(BiConsumer<Connection<T>, Throwable> callback) {
        this.errorCallback = callback;
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
    public MultiConnectionBuilder<T> withMessageIOFactory(MessageIOFactory factory) {
        if ((messageReaderFactory == null) && (messageWriterFactory == null)) {
            this.messageReaderFactory = factory;
            this.messageWriterFactory = factory;
        } else {
            if (messageReaderFactory != null) {
                throw new IllegalStateException("MessageReaderFactory already set: " + messageReaderFactory);
            }
            throw new IllegalStateException("MessageWriterFactory already set: " + messageWriterFactory);
        }
        return this;
    }

    /**
     * Build a connection with the given ExecutionContext.
     *
     * @param context The context in which the Connection will execute messages.
     *
     * @return This ConnectionBuilder
     */
    public MultiConnectionBuilder<T> withExecutionContextFactory(ExecutionContextFactory context) {
        if (executionContextFactory == null) {
            executionContextFactory = context;
        } else {
            throw new IllegalStateException("Execution context is already set");
        }
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param task The task to be executed; a Consumer accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public MultiConnectionBuilder<T> withContextualTask(Consumer<T> task) {
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
    public final MultiConnectionBuilder<T> withContextualTasks(Consumer<T>... tasks) {
        Collections.addAll(contextualTasks, tasks);
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The tasks to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public MultiConnectionBuilder<T> withContextualTasks(Collection<Consumer<T>> tasks) {
        contextualTasks.addAll(tasks);
        return this;
    }

    /**
     * Build a connection that will execute the given callback on shutdown.
     *
     * @param callback The callback to be invoked.
     *
     * @return This ReusableConnectionBuilder.
     */
    public MultiConnectionBuilder<T> withShutdownCallback(Consumer<Connection<T>> callback) {
        shutdownCallbacks.add(callback);
        return this;
    }
}
