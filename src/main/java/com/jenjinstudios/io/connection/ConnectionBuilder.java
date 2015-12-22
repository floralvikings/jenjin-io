package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Superclass for connection builders.
 *
 * @author Caleb Brinkman
 */
public class ConnectionBuilder<T extends ExecutionContext>
{
    private final Collection<Consumer<Connection<T>>> shutdownCallbacks = new LinkedList<>();
    private final Collection<Consumer<T>> contextualTasks = new LinkedList<>();
    private final Collection<Consumer<T>> recurringTasks = new LinkedList<>();
    private ExecutionContextFactory<T> executionContextFactory;
    private BiConsumer<Connection<T>, Throwable> errorCallback;
    private MessageReaderFactory readerFactory;
    private MessageWriterFactory writerFactory;

    /**
     * Build a Connection using the given Socket.
     *
     * @param socket The Socket that will back the given Connection.
     *
     * @return The built Connection.
     *
     * @throws IOException If there is an error retrieving input and output streams from the Socket.
     */
    public Connection<T> build(Socket socket) throws IOException {
        return build(socket.getInputStream(), socket.getOutputStream());
    }

    /**
     * Build a Connection using the given input and output streams.
     *
     * @param inputStream The {@code InputStream} used to read messages.
     * @param outputStream The {@code OutputStream} used to write messages.
     *
     * @return The built connection.
     */
    public Connection<T> build(InputStream inputStream, OutputStream outputStream) {
        return build(readerFactory.createReader(inputStream), writerFactory.createWriter(outputStream));
    }

    /**
     * Build a Connection using the given {@code MessageReader} and {@code MessageWriter}.
     *
     * @param reader The {@code MessageReader} used to read messages.
     * @param writer The {@code MessageWriter} used to write messages.
     *
     * @return The built connection.
     */
    public Connection<T> build(MessageReader reader, MessageWriter writer) {
        return new Connection(
              executionContextFactory.createInstance(),
              reader,
              writer,
              errorCallback,
              contextualTasks,
              shutdownCallbacks,
              recurringTasks
        );
    }

    /**
     * Use the given MessageIOFactory to create MessageReader and MessageWriter instances from Java Input and Output
     * streams.
     *
     * @param factory The MessageIOFactory.
     *
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder<T> withMessageIOFactory(MessageIOFactory factory) {
        if((readerFactory == null) && (writerFactory == null)) {
            readerFactory = factory;
            writerFactory = factory;
        }else if(readerFactory == null) {
            throw new IllegalStateException("MessageWriterFactory already set");
        }else {
            throw new IllegalStateException("MessageReaderFactory already set");
        }
        return this;
    }

    /**
     * Use the specified MessageReaderFactory to build a MessageReader from any InputStreams.
     *
     * @param factory The MessageReaderFactory.
     *
     * @return This SingleConnectionBuilder.
     */
    public ConnectionBuilder<T> withMessageReaderFactory(MessageReaderFactory factory) {
        if (this.readerFactory == null) {
            this.readerFactory = factory;
        } else {
            throw new IllegalStateException("MessageReaderFactory already set");
        }
        return this;
    }

    /**
     * Use the specified MessageWriterFactory to build a MessageWriter from any OutputStreams.
     *
     * @param factory The MessageWriterFactory.
     *
     * @return This SingleConnectionBuilder.
     */
    public ConnectionBuilder<T> withMessageWriterFactory(MessageWriterFactory factory) {
        if (this.writerFactory == null) {
            this.writerFactory = factory;
        } else {
            throw new IllegalStateException("MessageWriterFactory already set");
        }
        return this;
    }

    /**
     * Build a connection with the given error callback function.
     *
     * @param callback The Consumer (accepting a Connection and Throwable) that will be invoked when an error is
     * encountered.
     *
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder<T> withErrorCallback(BiConsumer<Connection<T>, Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param callbacks The task(s) to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder<T> withContextualTasks(Consumer<T>... callbacks) {
        Collections.addAll(contextualTasks, callbacks);
        return this;
    }

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The task(s) to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder<T> withShutdownCallbacks(Consumer<Connection<T>>... tasks) {
        Collections.addAll(shutdownCallbacks, tasks);
        return this;
    }

    /**
     * Build a connection with the given ExecutionContext.
     *
     * @param context The context in which the Connection will execute messages.
     *
     * @return This ConnectionBuilder
     */
    public ConnectionBuilder<T> withExecutionContextFactory(ExecutionContextFactory<T> context) {
        if (executionContextFactory == null) {
            executionContextFactory = context;
        } else {
            throw new IllegalStateException("Execution context is already set");
        }
        return this;
    }
}
