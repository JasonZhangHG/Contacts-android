package cool.contacts.android.log;

import com.blankj.utilcode.util.LogUtils;

public class ContactsLog {

    private final static String TAG = "ContactsLog";

    private String tag;

    public ContactsLog(String tag) {
        this.tag = tag;
    }

    public void v(String msg) {
        LogUtils.vTag(tag, msg);
    }

    public void d(String msg) {
        LogUtils.dTag(tag, msg);
    }

    public void i(String msg) {
        LogUtils.iTag(tag, msg);
    }

    public void w(String msg) {
        LogUtils.wTag(tag, msg);
    }

    public void e(String msg) {
        LogUtils.eTag(tag, msg);
    }

    public void e(String msg, Throwable throwable) {
        LogUtils.eTag(tag, msg, throwable);
    }

    public static int v(String tag, String msg) {
        LogUtils.vTag(tag, msg);
        return 1;
    }

    public static int d(String tag, String msg) {
        LogUtils.dTag(tag, msg);
        return 1;
    }

    public static int i(String tag, String msg) {
        LogUtils.iTag(tag, msg);
        return 1;
    }

    public static int w(String tag, String msg) {
        LogUtils.wTag(tag, msg);
        return 1;
    }

    public static int e(String tag, String msg) {
        LogUtils.eTag(tag, msg);
        return 1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        LogUtils.eTag(tag, msg, tr);
        return 1;
    }

    public static int json(String json) {
        LogUtils.json(json);
        return 1;
    }


    // print it anyway.
    public static int V(String msg) {
        return v(TAG, msg);
    }

    public static int D(String msg) {
        return d(TAG, msg);
    }

    public static int I(String msg) {
        return i(TAG, msg);
    }

    public static int W(String msg) {
        return w(TAG, msg);
    }

    public static int E(String msg) {
        return e(TAG, msg);
    }

    public static int E(String msg, Throwable tr) {
        return e(TAG, msg, tr);
    }

}