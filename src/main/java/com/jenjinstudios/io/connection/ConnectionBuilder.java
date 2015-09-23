package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.MessageIOFactory;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.BiConsumer;

/**
 * Used to configure and create a Connection.
 *
 * @author Caleb Brinkman
 */
public class ConnectionBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionBuilder.class);
    private MessageIOFactory messageIOFactory;
    private MessageReader messageReader;
    private MessageWriter messageWriter;
    private ExecutionContext executionContext;
    private BiConsumer<Connection, Throwable> errorCallback;

    /**
     * Build a connection using all the values supplied to this builder.
     *
     * @return A connection built with all the values supplied to this builder.
     */
    public Connection build() {
        if (messageReader == null) { throw new IllegalStateException("MessageReader not set"); }
        if (messageWriter == null) { throw new IllegalStateException("MessageWriter not set"); }
        if (executionContext == null) { throw new IllegalStateException("Execution Context not set"); }

        return new Connection(executionContext, messageReader, messageWriter, errorCallback);
    }

    /**
     * Construct a new ConnectionBuilder that will establish a connection over the given socket.
     *
     * @param socket The socket over which the connection will be made.
     *
     * @throws IOException If there is an exception when creating streams from the given socket.
     */
    public void withSocket(Socket socket) throws IOException {
        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();
        withInputStream(inputStream);
        withOutputStream(outputStream);
    }

    /**
     * Use the given MessageIOFactory to create MessageReader and MessageWriter instances from Java Input and Output
     * streams.
     *
     * @param factory The MessageIOFactory.
     */
    public void withMessageIOFactory(MessageIOFactory factory) {
        if (messageIOFactory == null) {
            if ((messageReader != null) || (messageWriter != null)) {
                LOGGER.warn("Applying MessageIOFactory after one or both streams have already been set");
            }
            this.messageIOFactory = factory;
        } else {
            throw new IllegalStateException("MessageIOFactory already set");
        }
    }

    /**
     * Build a connection with the given InputStream.
     *
     * @param inputStream The stream the Connection will use to read messages.
     */
    public void withInputStream(InputStream inputStream) {
        if (messageIOFactory == null) {
            throw new IllegalStateException("MessageIOFactory not set");
        }
        if (messageReader == null) {
            messageReader = messageIOFactory.createReader(inputStream);
        } else {
            throw new IllegalStateException("MessageReader is already set");
        }
    }

    /**
     * Build a connection with the given OutputStream.
     *
     * @param outputStream The output stream the Connection will use to write messages.
     */
    public void withOutputStream(OutputStream outputStream) {
        if (messageIOFactory == null) {
            throw new IllegalStateException("MessageIOFactory not set");
        }
        if (messageWriter == null) {
            messageWriter = messageIOFactory.createWriter(outputStream);
        } else {
            throw new IllegalStateException("MessageWriter is already set");
        }
    }

    /**
     * Build a connection with the given InputStream.
     *
     * @param inputStream The stream the Connection will use to read messages.
     */
    public void withInputStream(MessageReader inputStream) {
        if (messageReader == null) {
            messageReader = inputStream;
        } else {
            throw new IllegalStateException("MessageReader is already set");
        }
    }

    /**
     * Build a connection with the given OutputStream.
     *
     * @param outputStream The output stream the Connection will use to write messages.
     */
    public void withOutputStream(MessageWriter outputStream) {
        if (messageWriter == null) {
            messageWriter = outputStream;
        } else {
            throw new IllegalStateException("MessageWriter is already set");
        }
    }

    /**
     * Build a connection with the given ExecutionContext.
     *
     * @param context The context in which the Connection will execute messages.
     */
    public void withExecutionContext(ExecutionContext context) {
        if (executionContext == null) {
            executionContext = context;
        } else {
            throw new IllegalStateException("Execution context is already set");
        }
    }

    /**
     * Build a connection with the given error callback function.
     *
     * @param callback The Consumer (accepting a Connection and Throwable) that will be invoked when an error is
     * encountered.
     *
     * @return This ConnectionBuilder.
     */
    public ConnectionBuilder withErrorCallback(BiConsumer<Connection, Throwable> callback) {
        this.errorCallback = callback;
        return this;
    }
}
