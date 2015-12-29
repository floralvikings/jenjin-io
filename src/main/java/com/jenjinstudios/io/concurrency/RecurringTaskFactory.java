package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;

/**
 * Used for generating recurring tasks by a ConnectionBuilder.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface RecurringTaskFactory<T extends ExecutionContext>
{
    /**
     * Create a new RecurringTask.
     *
     * @return The newly created recurring task.
     */
    RecurringTask<T> createInstance();
}
