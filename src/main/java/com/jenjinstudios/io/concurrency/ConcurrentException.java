package com.jenjinstudios.io.concurrency;

/**
 * Thrown when there is an error in one of the IO threads.
 *
 * @author Caleb Brinkman
 */
class ConcurrentException extends RuntimeException
{
    ConcurrentException(Throwable cause) { super(cause); }

    ConcurrentException(String message) { super(message); }

    ConcurrentException(String message, Throwable cause) { super(message, cause); }
}
