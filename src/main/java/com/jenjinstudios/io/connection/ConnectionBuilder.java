package com.jenjinstudios.io.connection;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.MessageIOFactory;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface for classes used to build Connection objects.
 *
 * @author Caleb Brinkman
 */
public interface ConnectionBuilder
{
    /**
     * Build a connection using all the values supplied to this builder.
     *
     * @return A connection built with all the values supplied to this builder.
     */
    Connection build();

    /**
     * Construct a new ConnectionBuilder that will establish a connection over the given socket.
     *
     * @param socket The socket over which the connection will be made.
     *
     * @return This ConnectionBuilder.
     *
     * @throws IOException If there is an exception when creating streams from the given socket.
     */
    ConnectionBuilder withSocket(Socket socket) throws IOException;

    /**
     * Use the given MessageIOFactory to create MessageReader and MessageWriter instances from Java Input and Output
     * streams.
     *
     * @param factory The MessageIOFactory.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withMessageIOFactory(MessageIOFactory factory);

    /**
     * Build a connection with the given InputStream.
     *
     * @param inputStream The stream the Connection will use to read messages.
     *
     * @return This ConnectionBuilder
     */
    ConnectionBuilder withInputStream(InputStream inputStream);

    /**
     * Build a connection with the given OutputStream.
     *
     * @param outputStream The output stream the Connection will use to write messages.
     *
     * @return This ConnectionBuilder
     */
    ConnectionBuilder withOutputStream(OutputStream outputStream);

    /**
     * Build a connection with the given InputStream.
     *
     * @param reader The stream the Connection will use to read messages.
     *
     * @return This ConnectionBuilder
     */
    ConnectionBuilder withMessageReader(MessageReader reader);

    /**
     * Build a connection with the given OutputStream.
     *
     * @param writer The output stream the Connection will use to write messages.
     *
     * @return This ConnectionBuilder
     */
    ConnectionBuilder withMessageWriter(MessageWriter writer);

    /**
     * Build a connection with the given ExecutionContext.
     *
     * @param context The context in which the Connection will execute messages.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withExecutionContext(ExecutionContext context);

    /**
     * Build a connection with the given error callback function.
     *
     * @param callback The Consumer (accepting a Connection and Throwable) that will be invoked when an error is
     * encountered.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withErrorCallback(BiConsumer<Connection, Throwable> callback);

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param task The task to be executed; a Consumer accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withContextualTask(Consumer<ExecutionContext> task);

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The tasks to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withContextualTasks(Consumer<ExecutionContext>... tasks);

    /**
     * Build a connection that includes the given contextual task to be executed synchronously with message execution.
     *
     * @param tasks The tasks to be executed; Consumers accepting an ExecutionContext.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withContextualTasks(Iterable<Consumer<ExecutionContext>> tasks);

    /**
     * Build a connection that will invoke the given consumers when shutting down with itself as a parameter.
     *
     * @param callbacks The consumers.
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withShutdownCallbacks(Iterable<Consumer<Connection>> callbacks);

    /**
     * Build a connection that will invoke the given consumer when shutting down with itself as a parameter.
     *
     * @param callback The consumer
     *
     * @return This ConnectionBuilder.
     */
    ConnectionBuilder withShutdownCallback(Consumer<Connection> callback);
}
