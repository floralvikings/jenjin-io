package com.jenjinstudios.io.serialization

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.jenjinstudios.io.Message
import spock.lang.Specification

public class GsonDeserializerSpec extends Specification {

    def "GsonMessageDeserializer should deserialize JSON message into correct (adapted) class with properties"() {
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

    def "GsonMessageDeserializer should throw exception when no class is provided in JSON"() {
        given:
            def builder = new GsonBuilder();
            builder.registerTypeAdapter(Message, new GsonMessageDeserializer())
            def gson = builder.create();
            def json = '{"fields":{"name":"foo"}}'

        when:
            def message = gson.fromJson(json, Message)

        then:
            thrown(JsonParseException)
    }
}