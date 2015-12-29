package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.ConnectionBuilder;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to build Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerBuilder<T extends ExecutionContext>
{
    private final Collection<BiConsumer<Server, T>> contextualTasks = new LinkedList<>();
    private final Collection<Consumer<Connection<T>>> addedCallbacks = new LinkedList<>();
    private final Collection<Consumer<Connection<T>>> removedCallbacks = new LinkedList<>();
    private final Collection<Consumer<Server<T>>> startupCallbacks = new LinkedList<>();
    private final Collection<Consumer<Server<T>>> shutdownCallbacks = new LinkedList<>();
    private ServerSocket serverSocket;
    private ConnectionBuilder<T> connectionBuilder;

    /**
     * Build a Server using the properties supplied to this builder.
     *
     * @return The constructed Server
     *
     * @throws IllegalStateException If the ServerSocket or ReusableConnectionBuilder are not set.
     */
    public Server<T> build() {
        if (serverSocket == null) {
            throw new IllegalStateException("ServerSocket must be set to build server");
        }
        if (connectionBuilder == null) {
            throw new IllegalStateException("ReusableConnectionBuilder must be set to build server");
        }

        return new Server(
              serverSocket,
              connectionBuilder,
              contextualTasks,
              addedCallbacks,
              removedCallbacks,
              startupCallbacks,
              shutdownCallbacks
        );
    }

    /**
     * Build a Server using the given ServerSocket.
     *
     * @param socket The ServerSocket.
     *
     * @return This ServerBuilder.
     *
     * @throws IllegalStateException If the ServerSocket has already been set.
     */
    public ServerBuilder<T> withServerSocket(ServerSocket socket) {
        if (this.serverSocket == null) {
            this.serverSocket = socket;
        } else {
            throw new IllegalStateException("ServerSocket already set");
        }
        return this;
    }

    /**
     * Build a Server which will use the given ReusableConnectionBuilder to build new connections.
     *
     * @param builder The ReusableConnectionBuilder.
     *
     * @return This ServerBuilder.
     *
     * @throws IllegalStateException If the ReusableConnectionBuilder has already been set.
     */
    public ServerBuilder<T> withMultiConnectionBuilder(ConnectionBuilder<T> builder) {
        if (this.connectionBuilder == null) {
            this.connectionBuilder = builder;
        } else {
            throw new IllegalStateException("ReusableConnectionBuilder already set");
        }
        return this;
    }

    /**
     * Build a Server which will execute the given tasks each time a Connection executes its contextual tasks.
     *
     * @param tasks The tasks to be executed with access to this server and an ExecutionContext
     *
     * @return This ServerBuilder.
     */
    @SafeVarargs
    public final ServerBuilder<T> withContextualTasks(BiConsumer<Server, T>... tasks) {
        this.withContextualTasks(Arrays.asList(tasks));
        return this;
    }

    /**
     * Build a Server which will execute the given tasks each time a Connection executes its contextual tasks.
     *
     * @param tasks The tasks to be executed with access to this server and an ExecutionContext
     *
     * @return This ServerBuilder.
     */
    public ServerBuilder<T> withContextualTasks(Iterable<BiConsumer<Server, T>> tasks) {
        tasks.forEach(contextualTasks::add);
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is added.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder<T> withConnectionAddedCallbacks(Iterable<Consumer<Connection<T>>> callbacks) {
        callbacks.forEach(addedCallbacks::add);
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is added.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    @SafeVarargs
    public final ServerBuilder<T> withConnectionAddedCallbacks(Consumer<Connection<T>>... callbacks) {
        this.withConnectionAddedCallbacks(Arrays.asList(callbacks));
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is removed.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder<T> withConnectionRemovedCallbacks(Iterable<Consumer<Connection<T>>> callbacks) {
        callbacks.forEach(removedCallbacks::add);
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is removed.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    @SafeVarargs
    public final ServerBuilder<T> withConnectionRemovedCallbacks(Consumer<Connection<T>>... callbacks) {
        return withConnectionRemovedCallbacks(Arrays.asList(callbacks));
    }

    /**
     * Build a Server which will execute the given callbacks after startup.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder<T> withStartupCallbacks(Iterable<Consumer<Server<T>>> callbacks) {
        callbacks.forEach(startupCallbacks::add);
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks after startup.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    @SafeVarargs
    public final ServerBuilder<T> withStartupCallbacks(Consumer<Server<T>>... callbacks) {
        return withStartupCallbacks(Arrays.asList(callbacks));
    }

    /**
     * Build a Server which will execute the given callbacks after shutdown.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder<T> withShutdownCallbacks(Iterable<Consumer<Server<T>>> callbacks) {
        callbacks.forEach(shutdownCallbacks::add);
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks after shutdown.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    @SafeVarargs
    public final ServerBuilder<T> withShutdownCallbacks(Consumer<Server<T>>... callbacks) {
        return withShutdownCallbacks(Arrays.asList(callbacks));
    }
}
