package com.jenjinstudios.io.server;

import com.jenjinstudios.io.connection.ReusableConnectionBuilder;

import java.net.ServerSocket;

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
}
