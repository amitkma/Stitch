package amitkma.stitchlib.executors;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Create by Amit Kumar on 20/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Class which implements {@link ThreadFactory}. This class sets the priority of threads.
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    private static final String TAG = "PriorityThreadFactory";

    public PriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(@NonNull final Runnable r) {
        Runnable wrapper = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {
                    Log.e(TAG, "run: ", t);
                }
                r.run();
            }
        };
        return new Thread(wrapper);
    }
}
