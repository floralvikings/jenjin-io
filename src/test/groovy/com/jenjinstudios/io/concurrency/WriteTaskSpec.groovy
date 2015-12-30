package com.jenjinstudios.io.concurrency

import com.jenjinstudios.io.Message
import com.jenjinstudios.io.MessageWriter
import spock.lang.Specification

public class WriteTaskSpec extends Specification {
    def "WriteTask should write all outgoing messages from queue"() {
        given:
            def queue = Mock(MessageQueue)
            def message = Mock(Message)
            def message2 = Mock(Message)
            def writer = Mock(MessageWriter)
            def task = new WriteTask(queue, writer)

        when:
            queue.outgoingAndClear >> [message, message2]

        and:
            task.run()

        then:
            1 * writer.write(message)
            1 * writer.write(message2)
    }

    def "WriteTask should write no message if no message present in queue"() {
        given:
            def queue = Mock(MessageQueue)
            def writer = Mock(MessageWriter)
            def task = new WriteTask(queue, writer)

        when:
            queue.outgoingAndClear >> []

        and:
            task.run()

        then:
            0 * writer.write(_)

    }
}