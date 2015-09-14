package com.jenjinstudios.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.io.serialization.GsonMessageDeserializer;

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
    private final InputStream inputStream;

    /**
     * Construct a new GsonMessageReader that will read Messages from the given InputStream.
     * @param inputStream The InputStream.
     */
    public GsonMessageReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Message read() throws IOException {
        final String s = new DataInputStream(inputStream).readUTF();
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Message.class, new GsonMessageDeserializer())
              .create();
        return gson.fromJson(s, Message.class);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
