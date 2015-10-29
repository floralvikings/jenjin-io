package com.jenjinstudios.io.concurrency

import com.jenjinstudios.io.Message
import spock.lang.Specification

class MessageQueueSpec extends Specification{

    def "After calling getIncomingAndClear, incoming list should be empty"() {
        def message = Mock(Message)
        def queue = new MessageQueue()

        when:
            queue.messageReceived(message)

        then:
            queue.getIncomingAndClear().size() == 1
            queue.getIncomingAndClear().size() == 0
    }

    def "After calling getOutgoingAndClear, outgoing list should be empty"() {
        def message = Mock(Message)
        def queue = new MessageQueue()

        when:
            queue.queueOutgoing(message)

        then:
            queue.getOutgoingAndClear().size() == 1
            queue.getOutgoingAndClear().size() == 0
    }

    def "After calling getErrorsAndClear, error list should be empty"() {
        def throwable = Mock(Throwable)
        def queue = new MessageQueue()

        when:
            queue.errorEncountered(throwable)

        then:
            queue.getErrorsAndClear().size() == 1
            queue.getErrorsAndClear().size() == 0
    }
}
