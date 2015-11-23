package com.jenjinstudios.io;

/**
 * Convenience interface combining MessageReaderFactory and MessageWriterFactory.
 *
 * @author Caleb Brinkman
 * @see MessageReaderFactory
 * @see MessageWriterFactory
 */
public interface MessageIOFactory extends MessageReaderFactory, MessageWriterFactory {}
