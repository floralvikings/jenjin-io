package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTask.class);
    private final MessageQueue<T> messageQueue;
    private final T executionContext;
    private final Collection<Consumer<T>> contextualTasks;

    /**
     * Construct a new ExecuteTask that will execute messages from the given MessageQueue.
     *
     * @param messageQueue The MessageQueue.
     * @param executionContext The context in which messages should execute.
     * @param contextualTasks Tasks which should be invoked in synchronous fashion with the execution context.
     */
    public ExecutionTask(MessageQueue<T> messageQueue, T executionContext, Collection<Consumer<T>> contextualTasks)
    {
        this.messageQueue = messageQueue;
        this.executionContext = executionContext;
        this.contextualTasks = contextualTasks;
    }

    @Override
    public void run() {
        final List<Message> incoming = messageQueue.getIncomingAndClear();
        incoming.forEach(message -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Executing message (Type: {})", message.getClass().getName());
            }
            Message response = message.execute(executionContext);
            if (response != null) {
                messageQueue.queueOutgoing(response);
            }
            contextualTasks.forEach(consumer -> {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Executing contextual task: {}", consumer);
                }
                consumer.accept(executionContext);
            });
        });
        messageQueue.getRecurringTasks().forEach(consumer -> consumer.accept(executionContext));
    }
}
