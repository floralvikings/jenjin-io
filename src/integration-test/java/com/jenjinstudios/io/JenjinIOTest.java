package com.jenjinstudios.io;

import com.jenjinstudios.io.connection.Connection;
import com.jenjinstudios.io.connection.MultiConnectionBuilder;
import com.jenjinstudios.io.connection.SingleConnectionBuilder;
import com.jenjinstudios.io.serialization.GsonMessageIOFactory;
import com.jenjinstudios.io.server.Server;
import com.jenjinstudios.io.server.ServerBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * Full integration test of many Jenjin-IO features; may take a while to run and consume a large amount of resources.
 * <p>
 * This test creates and verifies behavior of a Client-Server Connection with multiple clients and a single server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class JenjinIOTest
{
    // Callbacks in the order in which they should be invoked
    private static final Consumer<Server> SERVER_STARTUP_CALLBACK = mock(Consumer.class);

    private static final Consumer<Connection> SERVER_CONN_ADDED_CALLBACK = mock(Consumer.class);

    private static final Consumer<ExecutionContext> MESSAGE_EXEC_TEST_CALLBACK = mock(Consumer.class);
    private static final BiConsumer<Server, ExecutionContext> SERVER_CONTEXT_TASK = mock(BiConsumer.class);
    private static final Consumer<ExecutionContext> SERVER_CONN_CONTEXT_TASK = mock(Consumer.class);

    private static final Consumer<Connection> CLIENT_00_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final Consumer<Connection> CLIENT_01_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final BiConsumer<Connection, Throwable> SERVER_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final Consumer<Connection> SERVER_CONN_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final Consumer<Connection> SERVER_CONN_REMOVED_CALLBACK = mock(Consumer.class);

    private static final Consumer<ExecutionContext> CLIENT_00_CONTEXT_TASK = mock(Consumer.class);
    private static final Consumer<ExecutionContext> CLIENT_01_CONTEXT_TASK = mock(Consumer.class);

    private static final BiConsumer<Connection, Throwable> CLIENT_00_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final BiConsumer<Connection, Throwable> CLIENT_01_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final Consumer<Server> SERVER_SHUTDOWN_CALLBACK = mock(Consumer.class);

    private static final int PORT = 51015;
    private static final String LOCALHOST = "127.0.0.1";
    private static final int BLOCK_TIME = 500;

    private Server server;

    /**
     * Begin the integration test.
     *
     * @throws IOException If there is an IOException during testing.
     */
    @SuppressWarnings("OverlyLongMethod")
    @Test
    public void coreFunctionalityTest() throws IOException {
        server = buildServer();
        server.start();                                             // Start the server

        block(BLOCK_TIME);
        verify(SERVER_STARTUP_CALLBACK).accept(server);             // Verify startup callbacks are invoked

        Connection client00 = buildConnection(0);                   // Build a client connection

        TestMessage testMessage00 = createMessage(0);               // Create a message (param of 0 means no resp)
        TestMessage testMessage01 = createMessage(1);               // Create a message (param of 2 means expect resp)

        TestMessage testBroadcast = createMessage(0);               // Create a test broadcast

        client00.start();                                           // Start the connection

        block(BLOCK_TIME);
        verify(SERVER_CONN_ADDED_CALLBACK).accept(any());           // Verify connection added callbacks are invoked

        client00.sendMessage(testMessage00);                        // Send the first message

        block(BLOCK_TIME);
        verify(MESSAGE_EXEC_TEST_CALLBACK).accept(any());           // Verify message was received by server
        verify(SERVER_CONTEXT_TASK).accept(any(), any());           // Verify server execution context task completes
        verify(SERVER_CONN_CONTEXT_TASK).accept(any());             // Verify connection exec context task completes


        Connection client01 = buildConnection(1);                   // Build another client connection
        client01.start();                                           // Start the connection

        block(BLOCK_TIME);
        final int i = server.getConnectionCount();
        Assert.assertEquals("Server should have 2 clients", 2, i);
        server.broadcast(testBroadcast);                         // Broadcast a Message to all clients (no resp)

        block(BLOCK_TIME);
        verify(CLIENT_00_CONTEXT_TASK, times(1)).accept(any());     // Verify all clients receive broadcast
        verify(CLIENT_01_CONTEXT_TASK, times(1)).accept(any());     // Verify all clients receive broadcast

        client00.stop();                                            // Shutdown the first client

        block(BLOCK_TIME);
        verify(CLIENT_00_SHUTDOWN_CALLBACK).accept(client00);       // Verify client shutdown callback is invoked
        verify(SERVER_ERROR_CALLBACK).accept(any(), any());         // Verify server error callback is invoked
        verify(SERVER_CONN_SHUTDOWN_CALLBACK).accept(any());        // Verify server connection is shutdown
        verify(SERVER_CONN_REMOVED_CALLBACK).accept(any());         // Verify client removed callback is invoked

        client01.sendMessage(testMessage01);                        // Send the message

        block(BLOCK_TIME);
        verify(CLIENT_01_CONTEXT_TASK, times(2)).accept(any());     // Verify client receives response

        block(BLOCK_TIME);
        server.stop();                                              // Stop the server

        block(BLOCK_TIME);
        verify(CLIENT_01_ERROR_CALLBACK).accept(eq(client01), any());// Verify client error callback is invoked
        verify(SERVER_SHUTDOWN_CALLBACK).accept(server);             // Verify shutdown callbacks are invoked.

    }

    /**
     * Used to shut down server if not null.
     */
    @After
    public void cleanUp() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    private static TestMessage createMessage(int value) {
        TestMessage testMessage01 = new TestMessage(); // Construct a new message
        testMessage01.setValue(value); // Set a non zero value
        return testMessage01;
    }

    private static Server buildServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        MultiConnectionBuilder connectionBuilder = new MultiConnectionBuilder();

        connectionBuilder.withMessageIOFactory(new GsonMessageIOFactory())
              .withExecutionContextFactory(TestExecutionContext::new)
              .withContextualTask(SERVER_CONN_CONTEXT_TASK)
              .withErrorCallback((connection, throwable) -> {
                  SERVER_ERROR_CALLBACK.accept(connection, throwable);
                  connection.stop();
              })
              .withShutdownCallback(SERVER_CONN_SHUTDOWN_CALLBACK);

        ServerBuilder serverBuilder = new ServerBuilder();
        serverBuilder.withServerSocket(serverSocket)
              .withMultiConnectionBuilder(connectionBuilder)
              .withStartupCallbacks(SERVER_STARTUP_CALLBACK)
              .withShutdownCallbacks(SERVER_SHUTDOWN_CALLBACK)
              .withConnectionAddedCallbacks(SERVER_CONN_ADDED_CALLBACK)
              .withConnectionRemovedCallbacks(SERVER_CONN_REMOVED_CALLBACK)
              .withContextualTasks(SERVER_CONTEXT_TASK);


        return serverBuilder.build();
    }

    private static Connection<TestExecutionContext> buildConnection(int clientNum) throws IOException {
        SingleConnectionBuilder connectionBuilder = new SingleConnectionBuilder();

        Socket socket = new Socket(LOCALHOST, PORT);

        connectionBuilder.withMessageIOFactory(new GsonMessageIOFactory())
              .withSocket(socket)
              .withExecutionContext(new TestExecutionContext());


        if (clientNum == 0) {
            connectionBuilder
                  .withContextualTask(CLIENT_00_CONTEXT_TASK)
                  .withErrorCallback(CLIENT_00_ERROR_CALLBACK)
                  .withShutdownCallback(CLIENT_00_SHUTDOWN_CALLBACK);
        } else if (clientNum == 1) {
            connectionBuilder
                  .withContextualTask(CLIENT_01_CONTEXT_TASK)
                  .withErrorCallback(CLIENT_01_ERROR_CALLBACK)
                  .withShutdownCallback(CLIENT_01_SHUTDOWN_CALLBACK);
        }

        return connectionBuilder.build();
    }

    private static void block(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            // ignore
        }
    }

    /**
     * Testing ExecutionContext.
     */
    public static class TestExecutionContext implements ExecutionContext
    {
        private int testValue;

        public int getTestValue() { return testValue; }

        public void setTestValue(int testValue) { this.testValue = testValue; }
    }

    /**
     * Test message from client to server.
     */
    public static class TestMessage implements Message<TestExecutionContext>
    {
        private int value;

        public void setValue(int value) { this.value = value; }

        @Override
        public Message execute(TestExecutionContext context) {
            MESSAGE_EXEC_TEST_CALLBACK.accept(context);
            TestMessage response = null;
            if (value > 0) {
                context.setTestValue(value);
                response = new TestMessage();
                response.setValue(value - 1);
            }
            return response;
        }
    }
}