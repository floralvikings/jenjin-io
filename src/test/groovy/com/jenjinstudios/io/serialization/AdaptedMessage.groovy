package com.jenjinstudios.io.serialization

import com.jenjinstudios.io.ExecutionContext
import com.jenjinstudios.io.Message
import com.jenjinstudios.io.annotations.MessageAdapter

/**
 * Used to test adapting messages.
 *
 * @author Caleb Brinkman
 */
@MessageAdapter(adaptFrom = "com.jenjinstudios.io.serialization.TestMessage")
class AdaptedMessage implements Message
{
    String name;

    @Override
    public Message execute(ExecutionContext context) {
        return null;
    }
}
