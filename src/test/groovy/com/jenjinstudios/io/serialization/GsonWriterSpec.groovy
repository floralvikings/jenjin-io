package com.jenjinstudios.io.serialization

import spock.lang.Specification

import java.nio.ByteBuffer

public class GsonWriterSpec extends Specification {
    def "GsonMessageWriter should serialize and write message object correctly"() {
        given:
            def json = '{"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"foo"}}'
            def message = new TestMessage()
            message.name = "foo"

        and:
            def out = new ByteArrayOutputStream()
            def writer = new GsonMessageWriter(out)

        when:
            writer.write(message)

        and:
            def jsonBytes = json.getBytes();
            def lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array();
            def bytes = new byte[(jsonBytes.length + lengthBytes.length)];

            System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
            System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length);

        then:
            out.toByteArray() == bytes
    }
}