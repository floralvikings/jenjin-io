package com.jenjinstudios.io.serialization;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;

/**
 * Test message implementation used for testing.
 *
 * @author Caleb Brinkman
 */
public class TestMessage implements Message
{
    private String name;

    @Override
    public Message execute(ExecutionContext context) {
        return null;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
