package com.jenjinstudios.io.stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.serialization.GsonMessageSerializer;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements a MessageOutputStream which reslies on Gson to serialize outgoing data.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageWriter implements MessageWriter
{
    private final OutputStream outputStream;

    /**
     * Construct a new GsonMessageOutputStream that will write Messages to the given stream.
     * @param outputStream The output stream.
     */
    public GsonMessageWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(Message message) throws IOException {
        DataOutput dataOutputStream = new DataOutputStream(outputStream);
        Gson gson = new GsonBuilder()
              .registerTypeAdapter(Message.class, new GsonMessageSerializer())
              .create();
        final String json = gson.toJson(message, Message.class);
        dataOutputStream.writeUTF(json);
    }

    @Override
    public void close() throws IOException {
        outputStream.flush();
        outputStream.close();
    }
}
