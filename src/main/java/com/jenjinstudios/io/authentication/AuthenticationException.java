package com.jenjinstudios.io.authentication;

/**
 * Thrown when there is an unexpected error while authenticating.
 *
 * @author Caleb Brinkman
 */
public class AuthenticationException extends Exception
{
    /**
     * Construct a new AuthenticationException with the given message and cause.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public AuthenticationException(String message, Throwable cause) { super(message, cause); }
}
