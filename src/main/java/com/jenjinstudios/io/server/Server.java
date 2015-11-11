package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.ReusableConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to accept and manage incoming connections.
 *
 * @author Caleb Brinkman
 */
public class Server
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int EXECUTOR_THREADS = 4;
    private final ScheduledExecutorService executor;
    private final ServerSocket serverSocket;
    private final ReusableConnectionBuilder connectionBuilder;
    private final Collection<BiConsumer<Server, ExecutionContext>> contextualTasks;
    private final Collection<Consumer<Connection>> connectionAddedCallbacks;
    private final Collection<Consumer<Connection>> connectionRemovedCallbacks;
    private final Collection<Consumer<Server>> startupCallbacks;
    private final Collection<Consumer<Server>> shutdownCallbacks;
    private final Collection<Connection> connections;

    Server(
          ServerSocket serverSocket,
          ReusableConnectionBuilder connectionBuilder,
          Collection<BiConsumer<Server, ExecutionContext>> contextualTasks,
          Collection<Consumer<Connection>> addedCallbacks,
          Collection<Consumer<Connection>> removedCallbacks,
          Collection<Consumer<Server>> startupCallbacks,
          Collection<Consumer<Server>> shutdownCallbacks)
    {
        this.serverSocket = serverSocket;
        this.connectionBuilder = connectionBuilder;
        this.contextualTasks = new LinkedList<>(contextualTasks);
        this.connectionAddedCallbacks = new LinkedList<>(addedCallbacks);
        this.connectionRemovedCallbacks = new LinkedList<>(removedCallbacks);
        this.startupCallbacks = new LinkedList<>(startupCallbacks);
        this.shutdownCallbacks = new LinkedList<>(shutdownCallbacks);
        executor = Executors.newScheduledThreadPool(EXECUTOR_THREADS);
        connections = Collections.synchronizedCollection(new LinkedList<>());

        this.connectionBuilder.withShutdownCallback(connection -> {
            connections.remove(connection);
            connectionRemovedCallbacks.forEach(consumer -> consumer.accept(connection));
        });
    }

    /**
     * Stop listening for inbound connections and attempt to gracefully close all existing connections.
     */
    public void stop() {
        Collection<Connection> temp = new LinkedList<>();
        synchronized (connections) {
            temp.addAll(connections);
            temp.forEach(Connection::stop);
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("Error when closing ServerSocket", e);
        }

        shutdownCallbacks.forEach(consumer -> consumer.accept(this));
    }

    /**
     * Start listening for inbound connections.
     */
    public void start() {
        connectionBuilder.
              withContextualTask(context -> this.contextualTasks.forEach(consumer -> consumer.accept(this, context)));
        executor.scheduleWithFixedDelay(() -> {
            try {
                if (serverSocket.isClosed()) {
                    LOGGER.debug("Server Socket Closed");
                } else {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        Connection connection = connectionBuilder.build(socket);
                        connections.add(connection);
                        connectionAddedCallbacks.forEach(consumer -> consumer.accept(connection));
                        connection.start();
                    } else {
                        LOGGER.warn("ServerSocket returned null connection");
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error when attempting to accept incoming connection", e);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);

        startupCallbacks.forEach(consumer -> consumer.accept(this));
    }

    public int getConnectionCount() { return connections.size(); }
}
