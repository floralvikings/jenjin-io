package com.jenjinstudios.io;

/**
 * Read from a MessageInputStream, executed, or written to a MessageOutputStream.
 *
 * @author Caleb Brinkman
 */
public abstract class Message<C extends ExecutionContext>
{
    /**
     * Execute any actions required by the message.
     *
     * @param context The context in which this message should execute.
     *
     * @return The response to the message; return null for no response.
     */
    public abstract Message execute(C context);
}
