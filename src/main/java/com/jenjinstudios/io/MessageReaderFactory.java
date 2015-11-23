package com.jenjinstudios.io;

import java.io.InputStream;

/**
 * Used to create a MessageReader from an InputStream.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface MessageReaderFactory
{
    /**
     * Create a MessageReader from the given InputStream.
     *
     * @param inputStream The stream from which raw data will be read.
     *
     * @return A MessageReader backed by the given InputStream.
     */
    MessageReader createReader(InputStream inputStream);
}
