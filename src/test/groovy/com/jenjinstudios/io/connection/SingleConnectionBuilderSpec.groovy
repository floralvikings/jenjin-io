package com.jenjinstudios.io.connection

import com.jenjinstudios.io.*
import spock.lang.Specification

import java.util.function.BiConsumer
import java.util.function.Consumer

public class SingleConnectionBuilderSpec extends Specification {
    def "ConnectionBuilder should not build if MessageReader not set"() {
        given: "A ConnectionBuilder with all essential properties set except MessageReader"
            def writer = Mock(MessageWriter)
            def context = Mock(ExecutionContext)
            def builder = new SingleConnectionBuilder()
            builder.withMessageWriter(writer)
                    .withExecutionContext(context)

        when: "Connection is built"
            builder.build()

        then: "IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should not build if MessageWriter not set"() {
        given: "A ConnectionBuilder with all essential properties set except MessageWriter"
            def reader = Mock(MessageReader)
            def context = Mock(ExecutionContext)
            def builder = new SingleConnectionBuilder()
            builder.withMessageReader(reader)
                    .withExecutionContext(context)

        when: "Connection is built"
            builder.build()

        then: "IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should not build if ExecutionContext not set"() {
        given: "A ConnectionBuilder with all essential properties set except ExecutionContext"
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def builder = new SingleConnectionBuilder()
            builder.withMessageReader(reader)
                    .withMessageWriter(writer)

        when: "Connection is built"
            builder.build()

        then: "IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should successfully build if all essential properties set"() {
        given: "A ConnectionBuilder with all essential properties set"
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def context = Mock(ExecutionContext)
            def builder = new SingleConnectionBuilder()
            builder.withMessageReader(reader)
                    .withMessageWriter(writer)
                    .withExecutionContext(context)

        when: "Connection is built"
            def connection = builder.build()

        then: "Connection should not be null, no exception should be thrown"
            connection != null
            notThrown(IllegalStateException)
    }

    def "ConnectionBuilder should use socket InputStream and OutputStream"() {
        given: "A Socket, MessageIOFactory, and ConnectionBuilder"
            def socket = Mock(Socket)
            def factory = Mock(MessageIOFactory)
            def builder = new SingleConnectionBuilder()

        when: "The MessageIOFactory and Socket are set"
            builder.withMessageIOFactory(factory)
            builder.withSocket(socket)

        then:
            1 * socket.inputStream
            1 * socket.outputStream
    }

    def "ConnectionBuilder should throw exception when InputStream set with no IO factory"() {
        given:
            def input = Mock(InputStream)
            def builder = new SingleConnectionBuilder()

        when:
            builder.withInputStream(input)

        then:
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when OutputStream set with no IO factory"() {
        given:
            def output = Mock(OutputStream)
            def builder = new SingleConnectionBuilder()

        when:
            builder.withOutputStream(output)

        then:
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when InputStream set and already exists"() {
        given: "A ConnectionBuilder, InputStream, and MessageIOFactory that returns a mocked reader"
            def factory = Mock(MessageIOFactory)
            def input = Mock(InputStream)
            def builder = new SingleConnectionBuilder()
            factory.createReader(input) >> Mock(MessageReader)

        when: "Set both factory and input stream"
            builder.withMessageIOFactory(factory)
            builder.withInputStream(input)

        and: "Set input stream again"
            builder.withInputStream(input)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when OutputStream set and already exists"() {
        given: "A ConnectionBuilder, OutputStream, and MessageIOFactory that returns a mocked writer"
            def factory = Mock(MessageIOFactory)
            def output = Mock(OutputStream)
            def builder = new SingleConnectionBuilder()
            factory.createWriter(output) >> Mock(MessageWriter)

        when: "Set both factory and output stream"
            builder.withMessageIOFactory(factory)
            builder.withOutputStream(output)

        and: "Set output stream again"
            builder.withOutputStream(output)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when setting MessageIOFactory while already set"() {
        given: "A ConnectionBuilder, OutputStream, and MessageIOFactory that returns a mocked writer"
            def factory = Mock(MessageIOFactory)
            def builder = new SingleConnectionBuilder()

        when: "The factory is set"
            builder.withMessageIOFactory(factory)

        and: "The factory is set again"
            builder.withMessageIOFactory(factory)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when MessageReader set and already exists"() {
        given: "A ConnectionBuilder and MessageReader"
            def reader = Mock(MessageReader)
            def builder = new SingleConnectionBuilder()

        when: "Set reader"
            builder.withMessageReader(reader)

        and: "Set reader again"
            builder.withMessageReader(reader)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when MessageWriter set and already exists"() {
        given: "A ConnectionBuilder and MessageWriter"
            def writer = Mock(MessageWriter)
            def builder = new SingleConnectionBuilder()

        when: "Set writer"
            builder.withMessageWriter(writer)

        and: "Set writer again"
            builder.withMessageWriter(writer)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should throw exception when ExecutionContext set and already exists"() {
        given: "A ConnectionBuilder and ExecutionContext"
            def context = Mock(ExecutionContext)
            def builder = new SingleConnectionBuilder()

        when: "Set context"
            builder.withExecutionContext(context)

        and: "Set context again"
            builder.withExecutionContext(context)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ConnectionBuilder should properly pass callback to connection when built"() {
        given: "A ConnectionBuilder (with necessary properties) and BiConsumer<Connection, Throwable>"
            def builder = new SingleConnectionBuilder()
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def context = Mock(ExecutionContext)
            def callback = Mock(BiConsumer)
            def exception = Mock(IOException)
            builder.withMessageReader(reader)
                    .withMessageWriter(writer)
                    .withExecutionContext(context)

        and: "ErrorCallback is set and Connection is built"
            builder.withErrorCallback(callback)
            def connection = builder.build()

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

    def "ConnectionBuilder should properly pass contextual task to connection when built"() {
        given: "A ConnectionBuilder (with necessary properties) and Consumer"
            def builder = new SingleConnectionBuilder()
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def context = Mock(ExecutionContext)
            def message = Mock(Message)
            def task01 = Mock(Consumer)
            def task02 = Mock(Consumer)
            def task03 = Mock(Consumer)
            def task04 = Mock(Consumer)
            def taskList = Collections.singletonList(task02)
            builder.withMessageReader(reader)
                    .withMessageWriter(writer)
                    .withExecutionContext(context)

        and: "Message reader returns mock message"
            reader.read() >> message

        and: "Callbacks are set and Connection is built"
            builder.withContextualTask(task01)
                    .withContextualTasks(taskList)
                    .withContextualTasks(task03, task04)
            def connection = builder.build()

        when: "Connection starts"
            connection.start()

        and: "Wait for threads to run"
            Thread.sleep(100)

        then: "Callbacks should be called at least once with ExecutionContext"
            (1.._) * task01.accept(context)
            (1.._) * task02.accept(context)
            (1.._) * task03.accept(context)
            (1.._) * task04.accept(context)

        cleanup: "Close connection"
            connection.stop()
    }
}