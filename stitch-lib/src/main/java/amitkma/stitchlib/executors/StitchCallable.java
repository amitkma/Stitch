package amitkma.stitchlib.executors;

import java.util.concurrent.Callable;

/**
 * Create by Amit Kumar on 22/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

public class StitchCallable<T> implements Callable<T> {

    private final Callable<T> mCallable;

    public StitchCallable(Callable<T> callable) {
        this.mCallable = callable;
    }

    @Override
    public T call() throws Exception {
        return mCallable.call();
    }
}
