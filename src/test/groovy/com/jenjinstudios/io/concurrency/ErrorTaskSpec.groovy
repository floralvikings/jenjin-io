package com.jenjinstudios.io.concurrency

import spock.lang.Specification

import java.util.function.Consumer

/**
 * Specification and test for the ErrorTask class
 *
 * @author Caleb Brinkman
 */
class ErrorTaskSpec extends Specification {

    def "Error task should invoke callback when error encountered"() {
        def messageQueue = Mock(MessageQueue)
        def consumer = Mock(Consumer)
        def throwable = Mock(Throwable)

        def errorTask = new ErrorTask(messageQueue, consumer)

        when:
            errorTask.run()

        then:
            1 * messageQueue.getErrorsAndClear() >> [throwable]
            1 * consumer.accept(throwable)
    }

    def "Error task should not invoke callback when no error encountered"() {
        def messageQueue = Mock(MessageQueue)
        def consumer = Mock(Consumer)
        def throwable = Mock(Throwable)

        def errorTask = new ErrorTask(messageQueue, consumer)

        when:
            errorTask.run()

        then:
            1 * messageQueue.getErrorsAndClear() >> []
            0 * consumer.accept(throwable)
    }
}
