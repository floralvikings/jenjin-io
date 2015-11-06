package com.jenjinstudios.io.serialization

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import spock.lang.Specification

import java.nio.ByteBuffer

public class GsonReaderSpec extends Specification {
    def "GsonMessageReader should be able to correctly deserialize valid JSON into Adapted Message"() {
        given:
            def json = '{"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"foo"}}'
            // Have to do a little bit of hackery to get a proper byte-array (have to include length of string)
            def jsonBytes = json.getBytes("UTF-8")
            def lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array()
            def bytes = new byte[jsonBytes.length + lengthBytes.length]
            System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
            System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length)

        and:
            def inputStream = new ByteArrayInputStream(bytes)
            def reader = new GsonMessageReader(inputStream)

        when:
            def message = reader.read()

        then:
            message instanceof AdaptedMessage
            ((AdaptedMessage) message).getName().equals("foo");

        cleanup:
            reader.close()
    }

    def "GsonMessageReader should wrap and re-throw any syntax-related exceptions in JSON"() {
        given:
            // Notice the missing opening bracket
            def json = '"class":"com.jenjinstudios.io.serialization.TestMessage","fields":{"name":"foo"}}'
            // Have to do a little bit of hackery to get a proper byte-array (have to include length of string)
            def jsonBytes = json.getBytes("UTF-8")
            def lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array()
            def bytes = new byte[jsonBytes.length + lengthBytes.length]
            System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
            System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length)

        and:
            def inputStream = new ByteArrayInputStream(bytes)
            def reader = new GsonMessageReader(inputStream)

        when:
            def message = reader.read()

        then:
            def exception = thrown(IOException)
            exception.cause instanceof JsonSyntaxException

        cleanup:
            reader.close()
    }

    def "GsonMessageReader should wrap and re-throw any exceptions related to malformed Message objects"() {
        given:
            // Notice the missing class property
            def json = '{"fields":{"name":"foo"}}'
            // Have to do a little bit of hackery to get a proper byte-array (have to include length of string)
            def jsonBytes = json.getBytes("UTF-8")
            def lengthBytes = ByteBuffer.allocate(2).putChar((char) jsonBytes.length).array()
            def bytes = new byte[jsonBytes.length + lengthBytes.length]
            System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
            System.arraycopy(jsonBytes, 0, bytes, lengthBytes.length, jsonBytes.length)

        and:
            def inputStream = new ByteArrayInputStream(bytes)
            def reader = new GsonMessageReader(inputStream)

        when:
            def message = reader.read()

        then:
            def exception = thrown(IOException)
            exception.cause instanceof JsonParseException

        cleanup:
            reader.close()
    }
}