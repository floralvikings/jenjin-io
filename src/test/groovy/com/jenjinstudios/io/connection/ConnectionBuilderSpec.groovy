package com.jenjinstudios.io.connection

import com.jenjinstudios.io.*
import spock.lang.Specification

import java.util.function.BiConsumer

public class ConnectionBuilderSpec extends Specification {
    def "ConnectionBuilder should properly pass callback to connection when built"() {
        given: "A ConnectionBuilder (with necessary properties) and BiConsumer<Connection, Throwable>"
            def builder = new ConnectionBuilder()
            def ioFactory = Mock(MessageIOFactory)
            def contextFactory = Mock(ExecutionContextFactory)
            def reader = Mock(MessageReader)
            def callback = Mock(BiConsumer)
            def exception = Mock(IOException)
            def socket = Mock(Socket)

            contextFactory.createInstance() >> Mock(ExecutionContext)
            socket.inputStream >> Mock(InputStream)
            socket.outputStream >> Mock(OutputStream)
            ioFactory.createWriter(_) >> Mock(MessageWriter);
            ioFactory.createReader(_) >> reader

        and: "ErrorCallback is set and Connection is built"
            builder.withErrorCallback(callback)
                    .withExecutionContextFactory(contextFactory)
                    .withMessageIOFactory(ioFactory)
            def connection = builder.build(socket)

        and: "MessageReader reads, exception is thrown"
            reader.read() >> { throw exception }

        when: "Connection starts"
            connection.start()

        and: "Wait for threads to run"
            Thread.sleep(100)

        then: "Callback should be called with exception and connection"
            1 * callback.accept(connection, exception)

        cleanup: "Close connection"
            connection.stop()
    }

    def "ConnectionBuilder should throw IllegalStateException if MessageIOFactory set twice"() {
        given: "A ConnectionBuilder and mock MessageIOFactory"
            def builder = new ConnectionBuilder()
            def factory = Mock(MessageIOFactory)

        when: "MessageIOFactory is set twice"
            builder.withMessageIOFactory(factory)
                    .withMessageIOFactory(factory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should utilize Socket streams during build"() {
        given: "A Mocked Socket, MessageIOFactory, ExecutionContextFactory, and Streams"
            def socket = Mock(Socket);
            def ioFactory = Mock(MessageIOFactory)
            def contextFactory = Mock(ExecutionContextFactory)
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def inStream = Mock(InputStream)
            def outStream = Mock(OutputStream)

        and: "A ConnectionBuilder"
            def connectionBuilder = new ConnectionBuilder()

        when: "The connection is built"
            connectionBuilder
                    .withMessageIOFactory(ioFactory)
                    .withExecutionContextFactory(contextFactory)
                    .build(socket);

        then:
            1 * socket.inputStream >> inStream
            1 * socket.outputStream >> outStream
            1 * ioFactory.createReader(inStream) >> reader
            1 * ioFactory.createWriter(outStream) >> writer
    }

    def "ConnectionBuilder should throw IllegalStateException if reader factory set twice"() {
        given: "A mock reader factory"
            def readerFactory = Mock(MessageReaderFactory)

        and: "A ConnectionBuilder"
            def connectionBuilder = new ConnectionBuilder();

        when: "The reader factory is set twice"
            connectionBuilder.withMessageReaderFactory(readerFactory)
                    .withMessageReaderFactory(readerFactory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw IllegalStateException if writer factory set twice"() {
        given: "A mock writer factory"
            def writerFactory = Mock(MessageWriterFactory)

        and: "A ConnectionBuilder"
            def connectionBuilder = new ConnectionBuilder();

        when: "The writer factory is set twice"
            connectionBuilder.withMessageWriterFactory(writerFactory)
                    .withMessageWriterFactory(writerFactory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw IllegalStateException if context factory set twice"() {
        given: "A mock execution context factory"
            def contextFactory = Mock(ExecutionContextFactory)

        and: "A ConnectionBuilder"
            def connectionBuilder = new ConnectionBuilder();

        when: "The context factory is set twice"
            connectionBuilder.withExecutionContextFactory(contextFactory)
                    .withExecutionContextFactory(contextFactory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }
}