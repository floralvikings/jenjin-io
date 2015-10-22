package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.ExecutionContextFactory;
import com.jenjinstudios.io.MessageIOFactory;

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
public class ReusableConnectionBuilder
{
    private ExecutionContextFactory executionContextFactory;
    private final Collection<Consumer<ExecutionContext>> contextualTasks = new LinkedList<>();
    private BiConsumer<Connection, Throwable> errorCallback;
    private MessageIOFactory messageIOFactory;

    /**
     * Build a connection using the given socket.
     *
     * @param socket The socket.
     *
     * @return The built connection.
     *
     * @throws IOException If there is an exception when building the connection.
     */
    public Connection build(Socket socket) throws IOException {
        return new ConnectionBuilder()
              .withMessageIOFactory(messageIOFactory)
              .withErrorCallback(errorCallback)
              .withContextualTasks(contextualTasks)
              .withExecutionContext(executionContextFactory.createInstance())
              .withSocket(socket)
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
    public ReusableConnectionBuilder withErrorCallback(BiConsumer<Connection, Throwable> callback) {
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
    public ReusableConnectionBuilder withMessageIOFactory(MessageIOFactory factory) {
        if (messageIOFactory == null) {
            this.messageIOFactory = factory;
        } else {
            throw new IllegalStateException("MessageIOFactory already set");
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
    public ReusableConnectionBuilder withExecutionContextFactory(ExecutionContextFactory context) {
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
    public ReusableConnectionBuilder withContextualTask(Consumer<ExecutionContext> task) {
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
    public final ReusableConnectionBuilder withContextualTasks(Consumer<ExecutionContext>... tasks) {
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
    public ReusableConnectionBuilder withContextualTasks(Collection<Consumer<ExecutionContext>> tasks) {
        contextualTasks.addAll(tasks);
        return this;
    }
}
