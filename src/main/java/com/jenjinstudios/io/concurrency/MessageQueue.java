package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;
import com.jenjinstudios.io.Message;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Queues incoming and outgoing messages in a thread-safe manner.
 *
 * @author Caleb Brinkman
 */
public class MessageQueue<T extends ExecutionContext>
{
    private final Collection<RecurringTask<T>> recurringTasks = new LinkedList<>();
    private final Collection<Message> incoming = new LinkedList<>();
    private final Collection<Message> outgoing = new LinkedList<>();
    private final Collection<Throwable> errors = new LinkedList<>();

    /**
     * Construct a new MessageQueue.
     */
    public MessageQueue() {
        this(Collections.emptyList());
    }

    /**
     * Construct a new MessageQueue with the given recurring tasks.
     * @param recurringTasks The recurring tasks.
     */
    public MessageQueue(Collection<RecurringTask<T>> recurringTasks) {
        this.recurringTasks.addAll(recurringTasks);
    }

    /**
     * Indicate that a message has been received and add it to the incoming queue.
     *
     * @param message The message that has been received.
     */
    public void messageReceived(Message message) {
        if (message != null) {
            synchronized (incoming) {
                incoming.add(message);
            }
        }
    }

    /**
     * Get all messages that have been received since the last time this method was called, and clear the internal
     * list.
     *
     * @return A List of messages that have been received since the last time this method was called.
     */
    public List<Message> getIncomingAndClear() {
        List<Message> temp;
        synchronized (incoming) {
            temp = new LinkedList<>(incoming);
            incoming.clear();
        }
        return temp;
    }

    /**
     * Queue an outgoing message.
     *
     * @param message The message to be queued.
     */
    public void queueOutgoing(Message message) {
        synchronized (outgoing) {
            outgoing.add(message);
        }
    }

    /**
     * Get all messages that have been queued since the last time this method was called, and clear the internal
     * list.
     *
     * @return A List of messages that have been queued since the last time this method was called.
     */
    public List<Message> getOutgoingAndClear() {
        List<Message> temp;
        synchronized (outgoing) {
            temp = new LinkedList<>(outgoing);
            outgoing.clear();
        }
        return temp;
    }

    public Collection<RecurringTask<T>> getRecurringTasks() {
        recurringTasks.removeIf(RecurringTask::isCancelled);
        return Collections.unmodifiableCollection(recurringTasks);
    }
}
