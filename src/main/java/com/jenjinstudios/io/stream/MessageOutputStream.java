package com.jenjinstudios.io.stream;

import com.jenjinstudios.io.Message;

import java.io.IOException;

/**
 * Used to write messages to an output stream.
 *
 * @author Caleb Brinkman
 */
public interface MessageOutputStream
{
    /**
     * Write the given message to the backing output stream.
     *
     * @param message The message to write.
     *
     * @throws IOException If there is an exception while writing the message.
     */
    void write(Message message) throws IOException;
}
