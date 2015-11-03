package com.jenjinstudios.io.serialization

import com.google.gson.GsonBuilder
import com.jenjinstudios.io.Message
import spock.lang.Specification

public class GsonDeserializerSpec extends Specification {

    def "GsonMessageDeserializer should deserialize JSON message into correct class with properties"() {
        given:
            def builder = new GsonBuilder();
            builder.registerTypeAdapter(Message, new GsonMessageDeserializer())
            def gson = builder.create();
            def json = '{"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"foo"}}'

        when:
            def message = gson.fromJson(json, Message)

        then:
            message instanceof AdaptedMessage
            ((AdaptedMessage) message).getName() == "foo"
    }
}