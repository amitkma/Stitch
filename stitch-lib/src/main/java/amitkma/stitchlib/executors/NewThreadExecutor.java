package amitkma.stitchlib.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Create by Amit Kumar on 22/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Class for implementation of single threaded for doing any submitted task on a new thread.
 */
public class NewThreadExecutor {

    private static NewThreadExecutor sInstance;
    private int mThreadCount = 0;

    private NewThreadExecutor() {
    }

    public static NewThreadExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new NewThreadExecutor();
        }
        return sInstance;
    }

    /**
     * Method for handling tasks which have a callable.
     *
     * @param callable Required callable instance.
     * @param <T> Generic.
     * @return T
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied,
     * and the thread is interrupted, either before or during the activity.
     * @throws ExecutionException Exception thrown when attempting to retrieve the result of a task
     * that aborted by throwing an exception.
     */
    public <T> T submitCallableTask(final Callable<T> callable)
            throws ExecutionException, InterruptedException {
        StitchCallable<T> stitchCallable = new StitchCallable<>(callable);
        FutureTask<T> futureTask = new FutureTask<T>(stitchCallable);
        Thread thread = new Thread(futureTask);
        thread.setName("New Thread " + mThreadCount);
        thread.start();
        increment();
        return futureTask.get();
    }

    /**
     * Method for handling tasks which have a runnable.
     *
     * @param runnable Required runnable instance.
     */
    public void submitVoidTask(final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public synchronized void increment() {
        mThreadCount++;
    }
}
