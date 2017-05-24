package amitkma.stitchlib.executors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Create by Amit Kumar on 20/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Singleton Class for implementation of UI Thread (also known as main thread) to execute task in UI
 * Thread.
 */
public final class UiExecutor implements Executor {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static UiExecutor sInstance;

    // Private constructor for implementation of singleton.
    private UiExecutor() {

    }

    /**
     * Method to get single instance of this class
     *
     * @return the single instance of {@link UiExecutor}
     */
    public static UiExecutor getInstance() {
        if (sInstance == null) {
            synchronized (UiExecutor.class) {
                sInstance = new UiExecutor();
            }
        }
        return sInstance;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        HANDLER.post(command);
    }
}
