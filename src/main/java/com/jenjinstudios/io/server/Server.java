package com.jenjinstudios.io.server;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.ConnectionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Used to accept and manage incoming connections.
 *
 * @author Caleb Brinkman
 */
public class Server<T extends ExecutionContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int EXECUTOR_THREADS = 4;
    private final ScheduledExecutorService executor;
    private final ServerSocket serverSocket;
    private final ConnectionBuilder<T> connectionBuilder;
    private final Collection<BiConsumer<Server, T>> contextualTasks;
    private final Collection<Consumer<Connection>> connectionAddedCallbacks;
    private final Collection<Consumer<Connection>> connectionRemovedCallbacks;
    private final Collection<Consumer<Server>> startupCallbacks;
    private final Collection<Consumer<Server>> shutdownCallbacks;
    private final Collection<Connection> connections;

    Server(
          ServerSocket serverSocket,
          ConnectionBuilder connectionBuilder,
          Iterable<BiConsumer<Server, T>> contextualTasks,
          Iterable<Consumer<Connection>> addedCallbacks,
          Iterable<Consumer<Connection>> removedCallbacks,
          Iterable<Consumer<Server>> startupCallbacks,
          Iterable<Consumer<Server>> shutdownCallbacks)
    {
        this.serverSocket = serverSocket;
        this.connectionBuilder = connectionBuilder;
        this.contextualTasks = new LinkedList<>();
        this.connectionAddedCallbacks = new LinkedList<>();
        this.connectionRemovedCallbacks = new LinkedList<>();
        this.startupCallbacks = new LinkedList<>();
        this.shutdownCallbacks = new LinkedList<>();

        executor = Executors.newScheduledThreadPool(EXECUTOR_THREADS);
        connections = Collections.synchronizedCollection(new LinkedList<>());

        contextualTasks.forEach(this.contextualTasks::add);
        addedCallbacks.forEach(this.connectionAddedCallbacks::add);
        removedCallbacks.forEach(this.connectionRemovedCallbacks::add);
        startupCallbacks.forEach(this.startupCallbacks::add);
        shutdownCallbacks.forEach(this.shutdownCallbacks::add);

        Consumer<Connection<T>> callback = connection -> {
            connections.remove(connection);
            connectionRemovedCallbacks.forEach(consumer -> consumer.accept(connection));
        };
        this.connectionBuilder.withShutdownCallbacks(callback);
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
        connectionBuilder.withContextualTasks((Consumer<T>) this::invokeContextualTasks);
        executor.scheduleWithFixedDelay(this::listenForConnection, 0, 10, TimeUnit.MILLISECONDS);

        startupCallbacks.forEach(consumer -> consumer.accept(this));
    }

    private void listenForConnection() {
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
        } catch (SocketException e) {
            LOGGER.info("SocketError encountered: " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("Error when attempting to accept incoming connection", e);
        }
    }

    /**
     * Broadcast the given message to all connections.
     *
     * @param message The message to be broadcast.
     */
    public void broadcast(Message message) { this.broadcast(message, connection -> true); }

    /**
     * Broadcast a message to all connections that fulfill the given predicate.
     *
     * @param message The message to be broadcast.
     * @param predicate The predicate which, if fulfilled, will cause a message to be broadcast by the given
     * connection.
     *
     * @return The number of Connections to which the message was broadcast.
     */
    public int broadcast(Message message, Predicate<Connection> predicate) {
        AtomicInteger sum = new AtomicInteger(0);
        connections.forEach(connection -> {
            if (predicate.test(connection)) {
                connection.sendMessage(message);
                sum.addAndGet(1);
            }
        });
        return sum.get();
    }

    private void invokeContextualTasks(T context) {
        this.contextualTasks.forEach(consumer -> consumer.accept(this, context));
    }

    public int getConnectionCount() { return connections.size(); }
}
