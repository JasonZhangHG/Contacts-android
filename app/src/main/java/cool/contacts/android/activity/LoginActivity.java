package cool.contacts.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cool.contacts.android.R;
import cool.contacts.android.base.BaseActivity;
import cool.contacts.android.utils.ResourceUtil;

//登陆
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText etusername;
    private EditText etpassword;
    private Button login;
    private Button sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, ResourceUtil.getString(R.string.bmob_key));
        setContentView(R.layout.activity_login);
        //缓存用户对象为空时， 可打开用户注册界面…
        initialize();
    }

    private void initialize() {
        etusername = (EditText) findViewById(R.id.et_username);
        etpassword = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        sign = (Button) findViewById(R.id.sign);
        sign.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                final String username = etusername.getText().toString();
                String password = etpassword.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    BmobUser userInfoBean = new BmobUser();
                    userInfoBean.setUsername(username);
                    userInfoBean.setPassword(password);
                    userInfoBean.login(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser userInfoBean, BmobException e) {
                            if (e == null) {
                                BmobUser currentUser = BmobUser.getCurrentUser(BmobUser.class);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sign:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
