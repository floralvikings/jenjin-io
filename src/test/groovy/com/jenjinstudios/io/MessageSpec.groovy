package com.jenjinstudios.io;

import spock.lang.Specification

public class MessageSpec extends Specification {
    def "Default execution should return null"() {
        given: "A simple Message implementation and mocked context"
            def context = Mock(ExecutionContext)
            def message = new Message() {}

        when: "Message is executed"
            def returnValue = message.execute(context)

        then: "Return value should be null"
            returnValue == null
    }
}