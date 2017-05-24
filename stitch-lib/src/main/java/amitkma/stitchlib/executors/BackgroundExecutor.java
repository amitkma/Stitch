package amitkma.stitchlib.executors;

import android.os.Process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Create by Amit Kumar on 20/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Singleton Class for implementation of ThreadPoolExecutor. This class create a thread pool of
 * fixed size depending on the available processors.
 */
public class BackgroundExecutor {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final ThreadPoolExecutor mThreadPoolExecutor;
    private static BackgroundExecutor sInstance;

    // Private constructor to make the class singleton.
    private BackgroundExecutor() {
        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(),
                new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND));
        mThreadPoolExecutor.prestartCoreThread();
    }

    // Static method to return the singleton instance.
    public static BackgroundExecutor getInstance() {
        if (sInstance == null) {
            synchronized (BackgroundExecutor.class) {
                sInstance = new BackgroundExecutor();
            }
        }
        return sInstance;
    }

    /**
     * Method for handling tasks which have a callable.
     * @param callable Required callable instance.
     * @param <T> Generic.
     * @return T
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public <T> T submitCallableTask(final Callable<T> callable)
            throws InterruptedException, ExecutionException {
        StitchCallable<T> stitchCallable = new StitchCallable<>(callable);
        return mThreadPoolExecutor.submit(stitchCallable).get();
    }

    /**
     * Method for handling tasks which have a runnable.
     * @param runnable Required runnable instance.
     */
    public void submitVoidTask(final Runnable runnable) {
        mThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }
}
