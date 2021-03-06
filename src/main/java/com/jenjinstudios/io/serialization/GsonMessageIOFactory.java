package com.jenjinstudios.io.serialization;

import com.jenjinstudios.io.MessageIOFactory;
import com.jenjinstudios.io.MessageReader;
import com.jenjinstudios.io.MessageWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Used to create GsonMessageReader and GsonMessageWriter instances from Java InputStream and OutputStream
 * instances.
 *
 * @author Caleb Brinkman
 */
public class GsonMessageIOFactory implements MessageIOFactory
{
    @Override
    public MessageReader createReader(InputStream inputStream) {
        return new GsonMessageReader(inputStream);
    }

    @Override
    public MessageWriter createWriter(OutputStream outputStream) {
        return new GsonMessageWriter(outputStream);
    }
}
