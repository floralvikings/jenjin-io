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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(JenjinIOTest.class);

    // Callbacks in the order in which they should be invoked
    private static final Consumer<Server<TestContext>> SERVER_STARTUP_CALLBACK = mock(Consumer.class);

    private static final Consumer<Connection<TestContext>> SERVER_CONN_ADDED_CALLBACK = mock(Consumer.class);

    private static final Consumer<TestContext> MESSAGE_EXEC_TEST_CALLBACK = mock(Consumer.class);
    private static final BiConsumer<Server, TestContext> SERVER_CONTEXT_TASK = mock(BiConsumer.class);
    private static final Consumer<TestContext> SERVER_CONN_CONTEXT_TASK = mock(Consumer.class);

    private static final Consumer<Connection<TestContext>> CLIENT_00_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final Consumer<Connection<TestContext>> CLIENT_01_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final BiConsumer<Connection<TestContext>, Throwable> SERVER_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final Consumer<Connection<TestContext>> SERVER_CONN_SHUTDOWN_CALLBACK = mock(Consumer.class);
    private static final Consumer<Connection<TestContext>> SERVER_CONN_REMOVED_CALLBACK = mock(Consumer.class);

    private static final Consumer<TestContext> CLIENT_00_CONTEXT_TASK = mock(Consumer.class);
    private static final Consumer<TestContext> CLIENT_01_CONTEXT_TASK = mock(Consumer.class);

    private static final BiConsumer<Connection<TestContext>, Throwable> CLIENT_00_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final BiConsumer<Connection<TestContext>, Throwable> CLIENT_01_ERROR_CALLBACK = mock(BiConsumer.class);
    private static final Consumer<Server<TestContext>> SERVER_SHUTDOWN_CALLBACK = mock(Consumer.class);

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
    @Test(timeout = 30000)
    public void coreFunctionalityTest() throws IOException {
        LOGGER.info("Building Server");
        server = buildServer();
        LOGGER.info("Starting Server");
        server.start();                                             // Start the server

        LOGGER.info("Verifying Startup Callbacks");
        verify(SERVER_STARTUP_CALLBACK, timeout(BLOCK_TIME)).accept(server); // Verify startup callbacks are invoked

        LOGGER.info("Building first Connection");
        Connection client00 = buildConnection(0);                   // Build a client connection

        TestMessage testMessage00 = createMessage(0);               // Create a message (param of 0 means no resp)
        TestMessage testMessage01 = createMessage(1);               // Create a message (param of 2 means expect resp)

        TestMessage testBroadcast = createMessage(0);               // Create a test broadcast

        LOGGER.info("Starting first Connection");
        client00.start();                                           // Start the connection

        LOGGER.info("Verifying \"Connection Added\" callback");
        verify(SERVER_CONN_ADDED_CALLBACK, timeout(BLOCK_TIME)).accept(any()); // Verify connection added callbacks are invoked

        LOGGER.info("Sending first message (No response expected)");
        client00.sendMessage(testMessage00);                        // Send the first message

        LOGGER.info("Verifying message execution");
        verify(MESSAGE_EXEC_TEST_CALLBACK, timeout(BLOCK_TIME)).accept(any()); // Verify message was received by server
        LOGGER.info("Verifying server contextual task execution");
        verify(SERVER_CONTEXT_TASK, timeout(BLOCK_TIME)).accept(any(), any()); // Verify server execution context task completes
        LOGGER.info("Verifying connection contextual task execution");
        verify(SERVER_CONN_CONTEXT_TASK, timeout(BLOCK_TIME)).accept(any());  // Verify connection exec context task completes

        LOGGER.info("Building Second Connection");
        Connection client01 = buildConnection(1);                   // Build another client connection
        LOGGER.info("Starting Second Connection");
        client01.start();                                           // Start the connection

        LOGGER.info("Waiting for second connection to be established");
        while(server.getConnectionCount() < 2) {
            LOGGER.debug("Still waiting for connection to be established");
        }

        LOGGER.info("Sending test broadcast");
        server.broadcast(testBroadcast);                         // Broadcast a Message to all clients (no resp)
        LOGGER.info("Verifying first connection received broadcast, and that contextual task was executed");
        verify(CLIENT_00_CONTEXT_TASK, timeout(BLOCK_TIME).times(1)).accept(any());     // Verify all clients receive broadcast
        LOGGER.info("Verifying second connection received broadcast, and that contextual task was executed");
        verify(CLIENT_01_CONTEXT_TASK, timeout(BLOCK_TIME).times(1)).accept(any());     // Verify all clients receive broadcast

        LOGGER.info("Shutting down first Connection");
        client00.stop();                                            // Shutdown the first client

        LOGGER.info("Verifying \"Shutdown Callback\" execution");
        verify(CLIENT_00_SHUTDOWN_CALLBACK, timeout(BLOCK_TIME)).accept(client00);       // Verify client shutdown callback is invoked
        LOGGER.info("Verifying \"Error Callback\" execution");
        verify(SERVER_ERROR_CALLBACK, timeout(BLOCK_TIME)).accept(any(), any());         // Verify server error callback is invoked
        LOGGER.info("Verifying Server-side \"Shutdown Callback\" execution");
        verify(SERVER_CONN_SHUTDOWN_CALLBACK, timeout(BLOCK_TIME)).accept(any());        // Verify server connection is shutdown
        LOGGER.info("Verifying \"Connection Removed\" execution");
        verify(SERVER_CONN_REMOVED_CALLBACK, timeout(BLOCK_TIME)).accept(any());         // Verify client removed callback is invoked

        LOGGER.info("Sending Second Message (Response Expected)");
        client01.sendMessage(testMessage01);                        // Send the message

        LOGGER.info("Verifying response received");
        verify(CLIENT_01_CONTEXT_TASK, timeout(BLOCK_TIME).times(2)).accept(any());     // Verify client receives response

        LOGGER.info("Stopping Server");
        server.stop();                                              // Stop the server

        LOGGER.info("Verifying client-side \"Error Callback\" execution");
        verify(CLIENT_01_ERROR_CALLBACK, timeout(BLOCK_TIME)).accept(eq(client01), any());// Verify client error callback is invoked
        LOGGER.info("Verifying server-side \"Shutdown Callback\" execution");
        verify(SERVER_SHUTDOWN_CALLBACK, timeout(BLOCK_TIME)).accept(server);             // Verify shutdown callbacks are invoked.

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

        MultiConnectionBuilder<TestContext> connectionBuilder = new MultiConnectionBuilder();

        connectionBuilder.withMessageIOFactory(new GsonMessageIOFactory())
              .withExecutionContextFactory(TestContext::new)
              .withContextualTask(SERVER_CONN_CONTEXT_TASK)
              .withErrorCallback((connection, throwable) -> {
                  SERVER_ERROR_CALLBACK.accept(connection, throwable);
                  connection.stop();
              })
              .withShutdownCallback(SERVER_CONN_SHUTDOWN_CALLBACK);

        ServerBuilder<TestContext> serverBuilder = new ServerBuilder();
        serverBuilder.withServerSocket(serverSocket)
              .withMultiConnectionBuilder(connectionBuilder)
              .withStartupCallbacks(SERVER_STARTUP_CALLBACK)
              .withShutdownCallbacks(SERVER_SHUTDOWN_CALLBACK)
              .withConnectionAddedCallbacks(SERVER_CONN_ADDED_CALLBACK)
              .withConnectionRemovedCallbacks(SERVER_CONN_REMOVED_CALLBACK)
              .withContextualTasks(SERVER_CONTEXT_TASK);


        return serverBuilder.build();
    }

    private static Connection<TestContext> buildConnection(int clientNum) throws IOException {
        SingleConnectionBuilder connectionBuilder = new SingleConnectionBuilder();

        Socket socket = new Socket(LOCALHOST, PORT);

        connectionBuilder.withMessageIOFactory(new GsonMessageIOFactory())
              .withSocket(socket)
              .withExecutionContext(new TestContext());


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

    /**
     * Testing ExecutionContext.
     */
    public static class TestContext implements ExecutionContext
    {
        private int testValue;

        public int getTestValue() { return testValue; }

        public void setTestValue(int testValue) { this.testValue = testValue; }
    }

    /**
     * Test message from client to server.
     */
    public static class TestMessage implements Message<TestContext>
    {
        private int value;

        public void setValue(int value) { this.value = value; }

        @Override
        public Message execute(TestContext context) {
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
