package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.ReusableConnectionBuilder;

import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to build Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerBuilder
{
    private ServerSocket serverSocket;

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
        return null;
    }

    /**
     * Build a Server using the given ServerSocket.`
     *
     * @param socket The ServerSocket.
     *
     * @return This ServerBuilder.
     */
    public ServerBuilder withServerSocket(ServerSocket socket) {
        this.serverSocket = socket;
        return this;
    }

    /**
     * Build a Server which will use the given ReusableConnectionBuilder to build new connections.
     *
     * @param builder The ReusableConnectionBuilder.
     *
     * @return This ServerBuilder.
     */
    public ServerBuilder withReusableConnectionBuilder(ReusableConnectionBuilder builder) {
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
