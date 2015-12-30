package com.jenjinstudios.io.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * Checks a given ScheduledFuture for errors, invoking a callback with the error if one is encountered.
 *
 * @author Caleb Brinkman
 */
public class ErrorTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorTask.class);
    private final Consumer<Throwable> errorCallback;
    private final ScheduledFuture future;

    /**
     * Construct a new ErrorTask that will monitor the supplied ScheduledFuture for exceptions and invoke the given
     * callback if one is encountered.
     *
     * @param future The ScheduledFuture.
     * @param errorCallback The callback to be invoked if an exception is encountered.
     */
    public ErrorTask(ScheduledFuture future, Consumer<Throwable> errorCallback) {
        this.future = future;
        this.errorCallback = errorCallback;
    }

    @Override
    public void run() {
        Throwable throwable = null;
        try {
            future.get();
        } catch (InterruptedException e) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error Thread Interrupted", e);
            }
        } catch (ExecutionException e) {
            LOGGER.error("Encountered ExecutionException", e.getCause());
            throwable = e;
        }
        if (throwable != null) {
            errorCallback.accept(throwable);
        }
    }
}
