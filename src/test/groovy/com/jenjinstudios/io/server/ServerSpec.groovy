package com.jenjinstudios.io.server

import com.jenjinstudios.io.connection.Connection
import com.jenjinstudios.io.connection.ReusableConnectionBuilder
import spock.lang.Specification

import java.util.function.Consumer

public class ServerSpec extends Specification {
    def "When started, Server should listen for and accept inbound connections"() {
        given: "A ServerSocket and Server"
            def serverSocket = Mock(ServerSocket)
            def server = new Server(serverSocket, null, [], [], [], [], [])

        when: "The server is started"
            server.start()
            Thread.sleep(100) // Give threads time to catch up

        then: "The ServerSocket should be accepting connections"
            (1.._) * serverSocket.accept()

        cleanup: "Stop the server"
            server.stop()
    }

    def "When stopped, Server should attempt to gracefully close all open connections"() {
        given: "A ServerSocket which returns a valid connection then blocks"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >>> [socket, null]
            def connection = Mock(Connection)
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            connectionBuilder.build(socket) >> connection

        and: "A Server using the given socket"
            def server = new Server(serverSocket, connectionBuilder, [], [], [], [], [])

        when: "The server is started"
            server.start()
            Thread.sleep(100) // Give threads time to catch up

        then: "One connection should be made"
            server.connectionCount == 1

        when: "The server is stopped"
            server.stop()
            Thread.sleep(100) // Give threads time to catch up

        then: "The connection should be closed and removed"
            1 * connection.stop()
            1 * serverSocket.close()
            server.connectionCount == 0
    }

    def "When Server starts, callback(s) should be invoked"() {
        given: "A Server with a start callback"
            def serverSocket = Mock(ServerSocket)
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, null, [], [], [], callbacks, [])

        when: "The Server is started"
            server.start()

        then: "The startup callback should be invoked"
            1 * callback.accept(server)

        cleanup: "Shutdown the server"
            server.stop()
    }

    def "When Server stops, callback(s) should be invoked"() {
        given: "A Server with a stop callback"
            def serverSocket = Mock(ServerSocket)
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, null, [], [], [], [], callbacks)

        when: "The Server is started"
            server.start()

        then:
            0 * callback.accept(server)

        and: "The Server stops"
            server.stop()

        then: "The callback should be invoked"
            1 * callback.accept(server)
    }

    def "When Server adds Connection, callback(s) should be invoked"() {
        given: "A ServerSocket which returns a mocked connection"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >> [socket, { while (true); }]
            def connection = Mock(Connection)
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            connectionBuilder.build(socket) >> connection

        and: "A Server using the given socket, and a connection added callback"
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], callbacks, [], [], [])

        when: "The Server is started and a Connection is added"
            server.start()

        then: "The callback should be invoked"
            1 * callback.accept(connection)

        cleanup: "Shutdown the server"
            server.stop()
    }

    def "When Server removes Connection, callback(s) should be invoked"() {
        given: "A ServerSocket which returns a mocked connection"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >> [socket, { while (true); }]
            def connection = Mock(Connection)
            def connectionBuilder = Mock(ReusableConnectionBuilder)
            connectionBuilder.build(socket) >> connection

        and: "A Server using the given socket, and a connection removed callback"
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], [], callbacks, [], [])

        when: "The Server is started and a Connection is added"
            server.start()

        and: "The connection is closed"
            Thread.sleep(100); // Give threads time to catch up
            connection.stop()

        then: "The callback should be invoked"
            Thread.sleep(100); // Give threads time to catch up
            1 * callback.accept(connection)

        cleanup: "Shutdown the server"
            server.stop()
    }
}