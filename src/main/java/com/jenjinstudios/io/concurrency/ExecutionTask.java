package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;

import java.util.List;

/**
 * Executes ExecutableMessage objects which have been read.
 *
 * @author Caleb Brinkman
 */
public class ExecutionTask implements Runnable
{
    private final MessageQueue messageQueue;
    private final ExecutionContext executionContext;

    /**
     * Construct a new ExecuteTask that will execute messages from the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param executionContext The context in which messages should execute.
     */
    public ExecutionTask(MessageQueue messageQueue, ExecutionContext executionContext) {
        this.messageQueue = messageQueue;
        this.executionContext = executionContext;
    }

    @Override
    public void run() {
        final List<Message> incoming = messageQueue.getIncomingAndClear();
        incoming.forEach(message -> {
            Message response = message.execute(executionContext);
            if (response != null) {
                messageQueue.queueOutgoing(response);
            }
        });
    }
}
