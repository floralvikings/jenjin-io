package com.jenjinstudios.io.serialization

import com.jenjinstudios.io.ExecutionContext
import com.jenjinstudios.io.Message

/**
 * Test message implementation used for testing.
 *
 * @author Caleb Brinkman
 */
class TestMessage implements Message
{
    String name;

    @Override
    public Message execute(ExecutionContext context) { return null; }
}
