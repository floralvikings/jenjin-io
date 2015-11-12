package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.ReusableConnectionBuilder;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to build Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerBuilder
{
    private final Iterable<BiConsumer<Server, ExecutionContext>> contextualTasks = new LinkedList<>();
    private final Iterable<Consumer<Connection>> addedCallbacks = new LinkedList<>();
    private final Iterable<Consumer<Connection>> removedCallbacks = new LinkedList<>();
    private final Iterable<Consumer<Server>> startupCallbacks = new LinkedList<>();
    private final Iterable<Consumer<Server>> shutdownCallbacks = new LinkedList<>();
    private ServerSocket serverSocket;
    private ReusableConnectionBuilder connectionBuilder;

    /**
     * Build a Server using the properties supplied to this builder.
     *
     * @return The constructed Server
     *
     * @throws IllegalStateException If the ServerSocket or ReusableConnectionBuilder are not set.
     */
    public Server build() {
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
    public ServerBuilder withServerSocket(ServerSocket socket) {
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
    public ServerBuilder withReusableConnectionBuilder(ReusableConnectionBuilder builder) {
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
    public final ServerBuilder withContextualTasks(BiConsumer<Server, ExecutionContext>... tasks) {
        return this;
    }

    /**
     * Build a Server which will execute the given tasks each time a Connection executes its contextual tasks.
     *
     * @param tasks The tasks to be executed with access to this server and an ExecutionContext
     *
     * @return This ServerBuilder.
     */
    public ServerBuilder withContextualTasks(Iterable<BiConsumer<Server, ExecutionContext>> tasks) {
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is added.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder withConnectionAddedCallbacks(Iterable<Consumer<Connection>> callbacks) {
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
    public final ServerBuilder withConnectionAddedCallbacks(Consumer<Connection>... callbacks) {
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks when a Connection is removed.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder withConnectionRemovedCallbacks(Iterable<Consumer<Connection>> callbacks) {
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
    public final ServerBuilder withConnectionRemovedCallbacks(Consumer<Connection>... callbacks) {
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks after startup.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder withStartupCallbacks(Iterable<Consumer<Connection>> callbacks) {
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
    public final ServerBuilder withStartupCallbacks(Consumer<Connection>... callbacks) {
        return this;
    }

    /**
     * Build a Server which will execute the given callbacks after shutdown.
     *
     * @param callbacks The callbacks to execute.
     *
     * @return This ServerBuilder
     */
    public ServerBuilder withShutdownCallbacks(Iterable<Consumer<Connection>> callbacks) {
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
    public final ServerBuilder withShutdownCallbacks(Consumer<Connection>... callbacks) {
        return this;
    }
}
