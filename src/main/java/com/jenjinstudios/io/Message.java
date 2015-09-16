package com.jenjinstudios.io;

/**
 * Read from a MessageReader, executed, or written to a MessageWriter.
 *
 * @author Caleb Brinkman
 */
public interface Message<C extends ExecutionContext>
{
    /**
     * Execute any actions required by the message.
     *
     * @param context The context in which this message should execute.
     *
     * @return The response to the message; return null for no response.
     */
    Message execute(C context);
}
