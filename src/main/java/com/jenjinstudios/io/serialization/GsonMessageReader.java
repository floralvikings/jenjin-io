package com.jenjinstudios.io.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a MessageWriter which relies on Gson to deserialize incoming data.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageReader implements MessageReader
{
    private final DataInputStream inputStream;
    private final Object gsonMessageDeserializer = new GsonMessageDeserializer();

    /**
     * Construct a new GsonMessageReader that will read Messages from the given InputStream.
     *
     * @param inputStream The InputStream.
     */
    public GsonMessageReader(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public Message read() throws IOException {
        final String s = (inputStream.available() > 0) ? inputStream.readUTF() : "";
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Message.class, gsonMessageDeserializer)
              .create();
        Message message;
        try {
            message = gson.fromJson(s, Message.class);
        } catch (JsonSyntaxException ex) {
            throw new IOException("Syntax error in message JSON", ex);
        } catch (JsonParseException ex) {
            throw new IOException("Unable to properly parse JSON data into Message object", ex);
        }
        return message;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
