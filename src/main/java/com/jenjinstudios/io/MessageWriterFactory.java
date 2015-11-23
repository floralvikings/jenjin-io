package com.jenjinstudios.io;

import java.io.OutputStream;

/**
 * Used to create a MessageWriter from an OutputStream.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface MessageWriterFactory
{
    /**
     * Create a MessageWriter from the given OutputStream.
     *
     * @param outputStream The stream to which raw data will be written.
     *
     * @return A MessageWriter backed by the given OutputStream.
     */
    MessageWriter createWriter(OutputStream outputStream);
}
