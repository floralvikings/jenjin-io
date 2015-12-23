package com.jenjinstudios.io.concurrency

import com.jenjinstudios.io.ExecutionContext
import com.jenjinstudios.io.Message
import spock.lang.Specification

import java.util.function.Consumer

/**
 * Specification and test for the ExecutionTask class
 *
 * @author Caleb Brinkman
 */
class ExecutionTaskSpec extends Specification {

    def "ExecutionTask should invoke Message#execute method and any callbacks"() {
        def messageQueue = Mock(MessageQueue)
        def consumer = Mock(Consumer)
        def message = Mock(Message)
        def context = Mock(ExecutionContext)

        messageQueue.getIncomingAndClear() >> [message]

        def task = new ExecutionTask(messageQueue, context, Collections.singletonList(consumer))

        when:
            task.run()

        then:
            1 * message.execute(context);
            1 * consumer.accept(context);
    }
}
