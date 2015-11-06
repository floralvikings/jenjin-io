package com.jenjinstudios.io.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for converting a message of one class into another class.
 *
 * @author Caleb Brinkman
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageAdapter
{
    /**
     * Specify the class from which the Message should be serialized.
     *
     * @return The name of the class from which the Message should be serialized.
     */
    String adaptFrom();
}
