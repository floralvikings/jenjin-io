package com.jenjinstudios.io.concurrency

import spock.lang.Specification

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.function.Consumer

/**
 * Specification and test for the ErrorTask class
 *
 * @author Caleb Brinkman
 */
class ErrorTaskSpec extends Specification {

    def "Error task should invoke callback when error encountered"() {
        def future = Mock(ScheduledFuture)
        def consumer = Mock(Consumer)
        def throwable = Mock(ExecutionException)

        def errorTask = new ErrorTask(future, consumer)

        when:
            errorTask.run()

        then:
            1 * future.get() >> { throw throwable } >> ""
            1 * consumer.accept(throwable)
    }

    def "Error task should not invoke callback when no error encountered"() {
        def future = Mock(ScheduledFuture)
        def consumer = Mock(Consumer)
        def throwable = Mock(Throwable)

        def errorTask = new ErrorTask(future, consumer)

        when:
            errorTask.run()

        then:
            1 * future.get() >> ""
            0 * consumer.accept(throwable)
    }
}
