package com.jenjinstudios.io.serialization;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.annotations.MessageAdapter;

/**
 * Used to test adapting messages.
 *
 * @author Caleb Brinkman
 */
@MessageAdapter(adaptFrom = TestMessage.class)
public class AdaptedMessage implements Message
{
    private String name;

    @Override
    public Message execute(ExecutionContext context) {
        return null;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
