package cool.contacts.android.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadExecutor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static ThreadExecutor INSTANCE;

    private int mPoolSize = CORE_POOL_SIZE;

    private Handler mHandler;

    private ScheduledExecutorService mExecutor;

    public static ThreadExecutor getInstance() {
        if (INSTANCE == null) {
            synchronized (ThreadExecutor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadExecutor();
                }
            }
        }
        return INSTANCE;
    }

    private ThreadExecutor() {
    }

    /**
     * Set pool size of thread.
     *
     * @param poolSize
     */
    public void setPoolSize(int poolSize) {
        if (poolSize > 0) {
            mPoolSize = poolSize;
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * Execute a runnable.
     *
     * @param task
     */
    public void execute(Runnable task) {
        ensureExecutor();
        mExecutor.execute(task);
    }

    /**
     * Submit a runnable.
     *
     * @param task
     * @return a Future representing pending completion of the task
     */
    public Future<?> submit(Runnable task) {
        ensureExecutor();
        return mExecutor.submit(task);
    }

    /**
     * Execute a runnable on UI thread.
     *
     * @param task
     */
    public void executeOnUI(Runnable task) {
        ensureHandler();
        mHandler.post(task);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay Time in milliseconds to before execution.
     */
    public void executeOnUIDelay(Runnable task, long delay) {
        ensureHandler();
        mHandler.postDelayed(task, delay);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay Time in milliseconds to before execution.
     */
    public void schedule(Runnable task, long delay) {
        schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute a runnable on UI thread after a specify time.
     *
     * @param task
     * @param delay
     * @param timeUnit
     */
    public void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        ensureExecutor();
        mExecutor.schedule(task, delay, timeUnit);
    }

    public Executor getExecutor() {
        ensureExecutor();
        return mExecutor;
    }

    public Handler getHandler() {
        ensureHandler();
        return mHandler;
    }

    private void ensureExecutor() {
        if (mExecutor == null) {
            synchronized (ThreadExecutor.class) {
                if (mExecutor == null) {
                    mExecutor = Executors.newScheduledThreadPool(mPoolSize);
                }
            }
        }
    }

    private void ensureHandler() {
        if (mHandler == null) {
            synchronized (ThreadExecutor.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }


}
