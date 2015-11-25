package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Executes ExecutableMessage objects which have been read.
 *
 * @author Caleb Brinkman
 */
public class ExecutionTask<T extends ExecutionContext> implements Runnable
{
    private final MessageQueue messageQueue;
    private final T executionContext;
    private final Collection<Consumer<T>> contextualTasks;

    /**
     * Construct a new ExecuteTask that will execute messages from the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param executionContext The context in which messages should execute.
     * @param contextualTasks Tasks which should be invoked in synchronous fashion with the execution context.
     */
    public ExecutionTask(MessageQueue messageQueue, T executionContext, Collection<Consumer<T>> contextualTasks)
    {
        this.messageQueue = messageQueue;
        this.executionContext = executionContext;
        this.contextualTasks = contextualTasks;
    }

    @Override
    public void run() {
        final List<Message> incoming = messageQueue.getIncomingAndClear();
        incoming.forEach(message -> {
            Message response = message.execute(executionContext);
            if (response != null) {
                messageQueue.queueOutgoing(response);
            }
            contextualTasks.forEach(consumer -> consumer.accept(executionContext));
        });
    }
}
