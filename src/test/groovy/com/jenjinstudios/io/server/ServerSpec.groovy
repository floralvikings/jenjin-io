package com.jenjinstudios.io.server

import com.jenjinstudios.io.Message
import com.jenjinstudios.io.connection.Connection
import com.jenjinstudios.io.connection.MultiConnectionBuilder
import spock.lang.Specification

import java.util.function.Consumer

public class ServerSpec extends Specification {
    def "When started, Server should listen for and accept inbound connections"() {
        given: "A ServerSocket and Server"
            def connectionBuilder = Mock(MultiConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            def server = new Server(serverSocket, connectionBuilder, [], [], [], [], [])

        when: "The server is started"
            server.start()
            Thread.sleep(100) // Give threads time to catch up

        then: "The ServerSocket should be accepting connections"
            1 * serverSocket.accept() >>> [{ while (true); }]

        cleanup: "Stop the server"
            server.stop()
    }

    def "When stopped, Server should attempt to gracefully close all open connections"() {
        given: "A ServerSocket which returns a valid connection then blocks"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >>> [socket, { while (true); }]
            def connection = Mock(Connection)
            def connectionBuilder = Mock(MultiConnectionBuilder)
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

        then: "The connection should be closed"
            1 * connection.stop()
            1 * serverSocket.close()
    }

    def "When Server starts, callback(s) should be invoked"() {
        given: "A Server with a start callback"
            def connectionBuilder = Mock(MultiConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            serverSocket.accept() >>> [{ while (true); }]
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], [], [], callbacks, [])

        when: "The Server is started"
            server.start()

        then: "The startup callback should be invoked"
            1 * callback.accept(server)

        cleanup: "Shutdown the server"
            server.stop()
    }

    def "When Server stops, callback(s) should be invoked"() {
        given: "A Server with a stop callback"
            def connectionBuilder = Mock(MultiConnectionBuilder)
            def serverSocket = Mock(ServerSocket)
            serverSocket.accept() >>> [{ while (true); }]
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], [], [], [], callbacks)

        when: "The Server is started"
            server.start()

        then:
            0 * callback.accept(server)

        when: "The Server stops"
            server.stop()

        then: "The callback should be invoked"
            1 * callback.accept(server)
    }

    def "When Server adds Connection, callback(s) should be invoked"() {
        given: "A ServerSocket which returns a mocked connection"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >> { args -> socket } >> m_block
            def connection = Mock(Connection)
            def connectionBuilder = Mock(MultiConnectionBuilder)
            connectionBuilder.build(socket) >> connection

        and: "A Server using the given socket, and a connection added callback"
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], callbacks, [], [], [])

        when: "The Server is started and a Connection is added"
            server.start()
            Thread.sleep(100) // Give Threads time to catch up

        then: "The callback should be invoked"
            1 * callback.accept(connection)

        cleanup: "Shutdown the server"
            server.stop()
    }

    def "When Server removes Connection, callback(s) should be invoked"() {
        given: "A ServerSocket which returns a mocked connection"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            serverSocket.accept() >>> [socket, { while (true); }]
            def connection = Mock(Connection)
            def connectionBuilder = Mock(MultiConnectionBuilder)
            connectionBuilder.build(socket) >> connection

        and: "A Server using the given socket, and a connection removed callback"
            def callback = Mock(Consumer)
            def callbacks = [callback]
            def server = new Server(serverSocket, connectionBuilder, [], [], callbacks, [], [])
            connection.stop() >> { callback.accept(connection) }

        when: "The Server is started and a Connection is added"
            server.start()
            Thread.sleep(100)

        and: "The connection is closed"
            server.stop()

        then: "The callback should be invoked"
            1 * callback.accept(connection)
    }

    def "When Server broadcasts, all connections should send message"() {
        given: "A ServerSocket which returns two mocked connections"
            def serverSocket = Mock(ServerSocket)
            def socket = Mock(Socket)
            def message = Mock(Message)
            serverSocket.accept() >>> [socket, socket, { while (true); }]
            def connection1 = Mock(Connection)
            def connection2 = Mock(Connection);
            def connectionBuilder = Mock(MultiConnectionBuilder)
            connectionBuilder.build(socket) >>> [connection1, connection2]

        and: "A Server using the given socket, and a connection removed callback"
            def server = new Server(serverSocket, connectionBuilder, [], [], [], [], [])

        when: "The Server is started and Connections are added"
            server.start()
            Thread.sleep(100)

        and: "The server sends a broadcast"
            server.broadcast(message)

        then: "The message should be sent by all connections"
            1 * connection1.sendMessage(message)
            1 * connection2.sendMessage(message)
    }

    def m_block = {}
}
