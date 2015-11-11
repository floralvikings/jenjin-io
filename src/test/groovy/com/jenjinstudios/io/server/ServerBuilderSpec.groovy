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
}