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

    def "GsonMessageSerializer should properly adapt message objects when serializing"() {
        given: "An AdaptToMessage and a GsonBuilder"
            def expectedJson = '{"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"bar"}}';
            def message = new AdaptToMessage()
            message.name = "bar"
            def builder = new GsonBuilder()

        when: "The GsonMessageSerializer is registered"
            builder.registerTypeAdapter(Message.class, new GsonMessageSerializer())
            def gson = builder.create();

        and: "The message is serialized"
            def json = gson.toJson(message, Message)

        then: "The message should be adapted from AdaptToMessage into TestMessage"
            json == expectedJson
    }
}