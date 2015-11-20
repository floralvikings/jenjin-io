package com.jenjinstudios.io.connection

import com.jenjinstudios.io.*
import spock.lang.Specification

import java.util.function.BiConsumer

public class MultiConnectionBuilderSpec extends Specification {
    def "MultiConnectionBuilder should properly pass callback to connection when built"() {
        given: "A MultiConnectionBuilder (with necessary properties) and BiConsumer<Connection, Throwable>"
            def builder = new MultiConnectionBuilder()
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

    def "MultiConnectionBuilder should throw IOException if MessageIOFactory set twice"() {
        given: "A MultiConnectionBuilder and mock MessageIOFactory"
            def builder = new MultiConnectionBuilder()
            def factory = Mock(MessageIOFactory)

        when: "MessageIOFactory is set twice"
            builder.withMessageIOFactory(factory)
                    .withMessageIOFactory(factory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }
}