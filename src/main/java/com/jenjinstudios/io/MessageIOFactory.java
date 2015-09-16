package com.jenjinstudios.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Used to create MessageReader and MessageWriter objects from java Input and Output streams.
 *
 * @author Caleb Brinkman
 */
public interface MessageIOFactory
{
    /**
     * Create a MessageReader from the given InputStream.
     *
     * @param inputStream The stream from which raw data will be read.
     *
     * @return A MessageReader backed by the given InputStream.
     */
    MessageReader createReader(InputStream inputStream);

    /**
     * Create a MessageWriter from the given OutputStream.
     *
     * @param outputStream The stream to which raw data will be written.
     *
     * @return A MessageWriter backed by the given OutputStream.
     */
    MessageWriter createWriter(OutputStream outputStream);
}
