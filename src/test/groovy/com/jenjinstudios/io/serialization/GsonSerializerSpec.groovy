package com.jenjinstudios.io.serialization

import com.google.gson.GsonBuilder
import com.jenjinstudios.io.Message
import spock.lang.Specification

public class GsonSerializerSpec extends Specification {
    def "GsonMessageSerializer should properly serialize message objects into JSON"() {
        given:
            def expectedJson = '{"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"foo"}}';
            def message = new TestMessage()
            message.name = "foo"

        and:
            def builder = new GsonBuilder()
            builder.registerTypeAdapter(Message.class, new GsonMessageSerializer())
            def gson = builder.create()

        when:
            def json = gson.toJson(message, Message)

        then:
            json == expectedJson
    }
}