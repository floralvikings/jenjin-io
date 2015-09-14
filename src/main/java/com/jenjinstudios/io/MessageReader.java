package com.jenjinstudios.io;

import com.jenjinstudios.io.Message;

import java.io.IOException;

/**
 * Used to read messages from an input stream.
 *
 * @author Caleb Brinkman
 */
public interface MessageReader
{
    /**
     * Read a message from the backing InputStream.
     * @return The message read from the backing input stream.
     * @throws IOException If there is an exception when reading the message.
     */
    Message read() throws IOException;

    /**
     * Close the underlying stream.
     * @throws IOException If there is an exception when closing the underlying stream.
     */
    void close() throws IOException;
}
