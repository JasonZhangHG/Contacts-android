package cool.contacts.android.activity;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cool.contacts.android.R;
import cool.contacts.android.base.BaseActivity;
import cool.contacts.android.base.BaseDataSource;
import cool.contacts.android.bean.ContactsInfo;
import cool.contacts.android.bean.UploadContactBean;
import cool.contacts.android.log.ContactsLog;
import cool.contacts.android.utils.DialogUtils;
import cool.contacts.android.utils.GetContactsInfoHelper;
import cool.contacts.android.utils.InputContactsHelper;
import cool.contacts.android.utils.PermissionUtil;
import cool.contacts.android.utils.ResourceUtil;

public class MainActivity extends BaseActivity {

    private final static ContactsLog logger = new ContactsLog(MainActivity.class.getSimpleName());

    private Button mUpload;
    private Button mDownload;
    private List<UploadContactBean> contactList = new ArrayList<>();
    private BmobUser bmobUser;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpload = findViewById(R.id.btn_upload_main_activity);
        mDownload = findViewById(R.id.btn_download_main_activity);
        Bmob.initialize(this, ResourceUtil.getString(R.string.bmob_key));
        getPermission(0);
        bmobUser = BmobUser.getCurrentUser();
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.checkSinglePermission(Manifest.permission.WRITE_CONTACTS)) {
                    startUploadContacts();
                } else {
                    getPermission(1);
                }

            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.checkSinglePermission(Manifest.permission.WRITE_CONTACTS)) {
                    startDownloadContacts();
                } else {
                    getPermission(2);
                }

            }
        });
    }

    public void startUploadContacts() {
        showProgressDialog();
        getContactsList();
    }

    public void startDownloadContacts() {
        showProgressDialog();
        downLoadContactsFromService();
    }

    public void getPermission(final int type) {
        PermissionUtils.permission(PermissionConstants.CONTACTS)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(final ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        if (type == 1) {
                            startUploadContacts();
                        } else if (type == 2) {
                            startDownloadContacts();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();
    }

    public void getContactsList() {

        GetContactsInfoHelper.getInstance().getContactList(new BaseDataSource.GetDataSourceCallback<List<ContactsInfo>>() {
            @Override
            public void onLoaded(@NonNull List<ContactsInfo> data) {
                for (ContactsInfo contactsInfo : data) {
                    if (contactsInfo != null && (!(TextUtils.isEmpty(contactsInfo.getName()))) && validPhoneNumber(contactsInfo.getPhone())) {
                        UploadContactBean uploadContactBean = new UploadContactBean(bmobUser.getUsername(), contactsInfo.getName(), contactsInfo.getPhone());
                        if (contactsInfo.getPhoto() == null) {
                            uploadContactBean.setAvatarUrl("");
                        } else {
                            uploadContactBean.setAvatarUrl(contactsInfo.getPhoto());
                        }
                        contactList.add(uploadContactBean);
                        LogUtils.d("getContactsList uploadContactBean = " + uploadContactBean);
                        uploadContactBean.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    logger.d("upload contacts 创建数据成功：" + s);
                                } else {
                                    logger.d("upload contacts 失败：" + e.getMessage() + "," + e.getErrorCode());
                                }
                            }
                        });
                    }
                }
                Collections.sort(contactList);
                logger.d("getContactsList  111  uploadContactRequest = " + contactList);
                stopProgressDialog();
                ToastUtils.showShort(ResourceUtil.getString(R.string.upload_contacts_success));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void downLoadContactsFromService() {
        BmobQuery<UploadContactBean> query = new BmobQuery<UploadContactBean>();
        query.addWhereEqualTo("userName", bmobUser.getUsername());
        query.order("-createdAt");
        query.setLimit(50);
        query.findObjects(new FindListener<UploadContactBean>() {
            @Override
            public void done(List<UploadContactBean> list, BmobException e) {
                if (e == null) {
                    logger.d("downLoadContactsFromService success = " + list.size());
                    inputContacts(list);
                } else {
                    logger.d("downLoadContactsFromService Failed  e = " + e);
                }
            }
        });
    }

    public void inputContacts(List<UploadContactBean> contactList) {

        InputContactsHelper.getInstance().inputContactList(new BaseDataSource.InputDataSourceCallback<String>() {

            @Override
            public void onFinish() {
                stopProgressDialog();
                ToastUtils.showShort(ResourceUtil.getString(R.string.download_contacts_success));
            }

            @Override
            public void onError() {
                stopProgressDialog();
                ToastUtils.showShort(ResourceUtil.getString(R.string.download_contacts_failed));
            }

            @Override
            public void onProgress(int progress) {
                logger.d("inputContacts  onProgress() progress = " + progress);
            }
        }, contactList);
    }

    private boolean validPhoneNumber(String phoneNum) {
        return phoneNum.length() >= 4 && phoneNum.length() <= 17;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.getInstance().getProgressDialog(MainActivity.this);
        }
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void stopProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            if (!isFinishing()) {
                mProgressDialog.dismiss();
            }
        }
    }
}
