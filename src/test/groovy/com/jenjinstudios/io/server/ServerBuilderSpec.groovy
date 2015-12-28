package com.jenjinstudios.io.server

import com.jenjinstudios.io.connection.ConnectionBuilder
import spock.lang.Specification

public class ServerBuilderSpec extends Specification {
    def "ServerBuilder should fail to build Server if ServerSocket not present"() {
        given: "A ServerBuilder with no ServerSocket present"
            def builder = new ServerBuilder();

        when: "The Server is built"
            builder.build()

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ServerBuilder should fail to build Server if ReusableConnectionBuilder not present"() {
        given: "A ServerBuilder with a ServerSocket and no ReusableConnectionBuilder"
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket)

        when: "The Server is built"
            builder.build()

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ServerBuilder should not fail to build Server if both ServerSocket and ReusableConnectionBuilder present"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null
    }

    def "ServerBuilder should throw exception if ServerSocket is set when alread extant"() {
        given: "A ServerBuilder with a ServerSocket"
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket)

        when: "The ServerSocket is set again"
            builder.withServerSocket(serverSocket)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ServerBuilder should throw exception if ReusableConnectionBuilder is set when alread extant"() {
        given: "A ServerBuilder with a ReusableConnectionBuilder"
            def serverSocket = Mock(ConnectionBuilder)
            def builder = new ServerBuilder()
            builder.withMultiConnectionBuilder(serverSocket)

        when: "The ReusableConnectionBuilder is set again"
            builder.withMultiConnectionBuilder(serverSocket)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ServerBuilder should pass contextual tasks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            def callbacks = Mock(Iterable)
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The builder is passed a callback"
            builder.withContextualTasks(callbacks)

        then: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Connection Added callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            def callbacks = Mock(Iterable)
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The builder is passed a callback"
            builder.withConnectionAddedCallbacks(callbacks)

        then: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Connection Removed callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            def callbacks = Mock(Iterable)
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The builder is passed a callback"
            builder.withConnectionRemovedCallbacks(callbacks)

        then: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Startup callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            def callbacks = Mock(Iterable)
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The builder is passed a callback"
            builder.withStartupCallbacks(callbacks)

        then: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Shutdown callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            def callbacks = Mock(Iterable)
            builder.withServerSocket(serverSocket).withMultiConnectionBuilder(connectionBuilder)

        when: "The builder is passed a callback"
            builder.withShutdownCallbacks(callbacks)

        then: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }
}