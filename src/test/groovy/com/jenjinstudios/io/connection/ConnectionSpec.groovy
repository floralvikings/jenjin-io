package com.jenjinstudios.io.connection

import com.jenjinstudios.io.ExecutionContext
import com.jenjinstudios.io.Message
import com.jenjinstudios.io.MessageReader
import com.jenjinstudios.io.MessageWriter
import spock.lang.Specification

public class ConnectionSpec extends Specification {

    def "Connection should automatically respond to incoming messages when running"() {
        given:
            def context = Mock(ExecutionContext)
            def reader = Mock(MessageReader)
            def writer = Mock(MessageWriter)
            def incoming = Mock(Message)
            def outgoing = Mock(Message)

            // Reader should report one incoming, then wait indefinitely for more
            reader.read() >> incoming
            incoming.execute(context) >> outgoing

            def connection = new Connection(context, reader, writer)

        when:
            connection.start()
            Thread.sleep(100)

        then:
            (1.._) * writer.write(outgoing)

        cleanup:
            connection.stop()
    }
}