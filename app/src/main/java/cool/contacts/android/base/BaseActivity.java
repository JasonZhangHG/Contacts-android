package cool.contacts.android.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cool.contacts.android.utils.ThreadExecutor;

public class BaseActivity extends AppCompatActivity {

    protected Handler mHandler;

    private static List<Activity> allActivity = new ArrayList<>();

    private static volatile Activity mCurrentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void registerActivity(Activity activity) {
        allActivity.add(activity);
    }

    protected void unRegisterActivity(final Activity activity) {
        if (getCurrentActivity() == activity) {
            setCurrentActivity(null);
        }
        allActivity.remove(activity);
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public static void finishAllActivity() {
        if (!ThreadExecutor.isMainThread()) {
            throw new IllegalStateException("Must called on main thread");
        }
        for (Activity activity : allActivity) {
            if (activity != null) {
                activity.finish();
            }
        }
        allActivity.clear();
    }


    public Handler getHandler() {
        if (mHandler == null) {
            synchronized (this) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    public void doInUI(Runnable runnable, long delayMillis) {
        getHandler().postDelayed(runnable, delayMillis);
    }

    public void toActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

}
