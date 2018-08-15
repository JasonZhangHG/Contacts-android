package cool.contacts.android.activity;

import android.os.Bundle;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cool.contacts.android.R;
import cool.contacts.android.base.BaseActivity;
import cool.contacts.android.utils.ResourceUtil;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //第一：默认初始化
        Bmob.initialize(this, ResourceUtil.getString(R.string.bmob_key));
        doInUI(new Runnable() {
            @Override
            public void run() {
                BmobUser bmobUser = BmobUser.getCurrentUser();
                if (bmobUser != null) {
                    // 允许用户使用应用
                    toActivity(MainActivity.class);
                    WelcomeActivity.this.finish();
                } else {
                    toActivity(LoginActivity.class);
                    WelcomeActivity.this.finish();
                }
            }
        }, 50);
    }
}

