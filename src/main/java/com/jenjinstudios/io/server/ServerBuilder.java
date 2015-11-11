package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.connection.ReusableConnectionBuilder;

import java.net.ServerSocket;
import java.util.function.BiConsumer;

/**
 * Used to build Server objects.
 *
 * @author Caleb Brinkman
 */
public class ServerBuilder
{
    /**
     * Build a Server using the properties supplied to this builder.
     *
     * @return The constructed Server
     *
     * @throws IllegalStateException If the ServerSocket or ReusableConnectionBuilder are not set.
     */
    public Server build() {
        return null;
    }

    /**
     * Build a Server using the given ServerSocket.
     *
     * @param socket The ServerSocket.
     *
     * @return This ServerBuilder.
     */
    public ServerBuilder withServerSocket(ServerSocket socket) {
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
}
