package com.jenjinstudios.io;

/**
 * Used to create ExecutionContext instances.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface ExecutionContextFactory<T extends ExecutionContext>
{
    /**
     * Create a new instance of an ExecutionContext.
     *
     * @return A new ExecutionContext.
     */
    T createInstance();
}
