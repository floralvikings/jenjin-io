package com.jenjinstudios.io.stream;

import com.jenjinstudios.io.Message;

import java.io.IOException;

/**
 * Used to write messages to an output stream.
 *
 * @author Caleb Brinkman
 */
public interface MessageWriter
{
    /**
     * Write the given message to the backing output stream.
     *
     * @param message The message to write.
     *
     * @throws IOException If there is an exception while writing the message.
     */
    void write(Message message) throws IOException;

    /**
     * Close the underlying stream.
     * @throws IOException If there is an exception when closing the underlying stream.
     */
    void close() throws IOException;
}
