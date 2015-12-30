package com.jenjinstudios.io.concurrency

import com.jenjinstudios.io.Message
import com.jenjinstudios.io.MessageReader
import spock.lang.Specification

class ReadTaskSpec extends Specification {
    def "ReadTask should pass incoming messages to MessageQueue"() {
        def message = Mock(Message)
        def queue = Mock(MessageQueue)
        def reader = Mock(MessageReader)
        def task = new ReadTask(queue, reader)

        reader.read() >> message

        when:
            task.run()

        then:
            1 * queue.messageReceived(message)
    }
}
