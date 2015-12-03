package com.jenjinstudios.io.serialization

import com.jenjinstudios.io.ExecutionContext
import com.jenjinstudios.io.Message
import com.jenjinstudios.io.annotations.MessageAdapter

/**
 * Used to test adapting messages.
 *
 * @author Caleb Brinkman
 */
@MessageAdapter(adaptTo = "com.jenjinstudios.io.serialization.TestMessage")
class AdaptToMessage implements Message{
    String name;

    @Override
    public Message execute(ExecutionContext context) {
        return null;
    }
}
