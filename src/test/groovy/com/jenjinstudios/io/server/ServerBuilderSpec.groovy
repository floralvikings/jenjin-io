package com.jenjinstudios.io.server

import com.jenjinstudios.io.connection.ReusableConnectionBuilder
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
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

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
            def serverSocket = Mock(ReusableConnectionBuilder)
            def builder = new ServerBuilder()
            builder.withReusableConnectionBuilder(serverSocket)

        when: "The ReusableConnectionBuilder is set again"
            builder.withReusableConnectionBuilder(serverSocket)

        then: "An IllegalStateException should be thrown"
            thrown(IllegalStateException)
    }

    def "ServerBuilder should pass contextual tasks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

        and: "A callback"
            def callbacks = Mock(Iterable)
            builder.withContextualTasks(callbacks)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null

        and: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Connection Added callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

        and: "A connection added callback"
            def callbacks = Mock(Iterable)
            builder.withConnectionAddedCallbacks(callbacks)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null

        and: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Connection Removed callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

        and: "A connection removed callback"
            def callbacks = Mock(Iterable)
            builder.withConnectionRemovedCallbacks(callbacks)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null

        and: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Startup callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

        and: "A startup callback"
            def callbacks = Mock(Iterable)
            builder.withStartupCallbacks(callbacks)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null

        and: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }

    def "ServerBuilder should pass Shutdown callbacks to built server"() {
        given: "A ServerBuilder with a ServerSocket and ReusableConnectionBuilder"
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def builder = new ServerBuilder()
            builder.withServerSocket(serverSocket).withReusableConnectionBuilder(connectionBuilder)

        and: "A shutdown callback"
            def callbacks = Mock(Iterable)
            builder.withShutdownCallbacks(callbacks)

        when: "The Server is built"
            def server = builder.build()

        then: "Server should not be null"
            server != null

        and: "Callbacks should have been iterated"
            1 * callbacks.forEach(_)
    }
}