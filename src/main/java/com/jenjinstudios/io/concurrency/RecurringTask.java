package com.jenjinstudios.io.concurrency;

import com.jenjinstudios.io.ExecutionContext;

import java.util.concurrent.TimeUnit;

/**
 * Represents a task with access to an ExecutionContext that should be repeated at a given interval until it is
 * cancelled.
 *
 * @author Caleb Brinkman
 */
public abstract class RecurringTask<T extends ExecutionContext>
{
    private boolean skipped;
    private boolean cancelled;
    private boolean paused;
    private final long interval;
    private long lastExecutionTime;

    /**
     * Construct a RecurringTask that should execute roughly once per second.
     */
    public RecurringTask() { this(TimeUnit.SECONDS); }

    /**
     * Construct a RecurringTask that should execute roughly once per the time unit specified.
     *
     * @param timeUnit The time unit that should pass between each execution.  Note that no time unit will ever cause
     * this task to be executed faster that once per millisecond.  Also note that this specifies the <i>minimum</i>
     * unit of time that must pass before the next time this task executes.
     */
    public RecurringTask(TimeUnit timeUnit) { this(timeUnit.toMillis(1)); }

    /**
     * Construct a RecurringTask that will execute roughly once every {@code interval} milliseconds.
     *
     * @param interval The number of milliseconds that should have passed between executions of this task.  Note that
     * is the <i>minimum</i> number of milliseconds; more may pass between each execution.
     */
    public RecurringTask(long interval) { this.interval = interval; }

    public final long getInterval() { return interval; }

    /**
     * This method is invoked by the execution thread when <i>at least</i> the number of milliseconds specified by
     * {@code getInterval} have passed since the last time it was executed.
     *
     * @param context The ExecutionContext provided to this RecurringTask, specific to the execution thread on which it
     * was scheduled.
     */
    public abstract void execute(T context);

    /**
     * Cancel this task; after this method is called, the task will not be run again.
     */
    public void cancel() { cancelled = true; }

    public boolean isCancelled() { return cancelled; }

    public boolean isPaused() { return paused; }

    public boolean isSkipped() { return skipped; }

    /**
     * Pause this task; it will not be executed again until it is resumed.
     */
    public void pause() { paused = true; }

    /**
     * Resume this task; if it is currently paused, it will begin executing at it's regular interval again.
     */
    public void resume() { paused = false; }

    /**
     * Skip the next execution of this task.
     */
    public void skip() { skipped = true; }

    final boolean shouldExecute(long currentTime) {
        boolean shouldExecute = !cancelled;
        shouldExecute &= !paused;
        shouldExecute &= !skipped;
        shouldExecute &= (currentTime - lastExecutionTime) >= interval;
        skipped = false;
        return shouldExecute;
    }

    final void done() { lastExecutionTime = System.currentTimeMillis(); }
}
