package com.jenjinstudios.io.stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.serialization.GsonMessageDeserializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a MessageInputStream which relies on Gson to deserialize incoming data.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageInputStream implements MessageInputStream
{
    private final InputStream inputStream;

    /**
     * Construct a new GsonMessageInputStream that will read Messages from the given InputStream.
     * @param inputStream The InputStream.
     */
    public GsonMessageInputStream(InputStream inputStream) {
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
}
