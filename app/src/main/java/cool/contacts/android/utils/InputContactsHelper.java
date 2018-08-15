package cool.contacts.android.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.List;

import cool.contacts.android.bean.UploadContactBean;
import cool.contacts.android.base.BaseDataSource;
import cool.contacts.android.base.CCApplication;

public class InputContactsHelper {

    private static volatile InputContactsHelper inputContactsHelper = null;

    private ContactAsyncInputHandler mCameraHandler;

    public InputContactsHelper() {

    }

    public static InputContactsHelper getInstance() {
        if (inputContactsHelper == null) {
            synchronized (InputContactsHelper.class) {
                if (inputContactsHelper == null) {
                    inputContactsHelper = new InputContactsHelper();
                }
            }
        }
        return inputContactsHelper;
    }

    public void inputContactList(final BaseDataSource.InputDataSourceCallback<String> callback, List<UploadContactBean> contactList) {

        if (mCameraHandler == null) {
            final ContactAsyncInputThread thread = new ContactAsyncInputThread(callback);
            thread.start();
            mCameraHandler = new ContactAsyncInputHandler(thread.getLooper(), thread);
        }
        mCameraHandler.inputContactList(contactList);
    }

    private final static class ContactAsyncInputHandler extends Handler {

        private ContactAsyncInputThread mThread;

        private static final int MSG_PREVIEW_START = 1;

        private ContactAsyncInputHandler(Looper looper, ContactAsyncInputThread thread) {
            super(looper);
            mThread = thread;
        }

        private void inputContactList(List<UploadContactBean> contactList) {
            sendMessage(obtainMessage(MSG_PREVIEW_START, contactList));
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_PREVIEW_START: {
                    mThread.inputContactList((List<UploadContactBean>) msg.obj);
                    break;
                }
                default:
                    throw new RuntimeException("unknown message:what=" + msg.what);
            }
        }
    }

    private final static class ContactAsyncInputThread extends HandlerThread {

        private BaseDataSource.InputDataSourceCallback<String> mCallback;

        public ContactAsyncInputThread(BaseDataSource.InputDataSourceCallback<String> callback) {
            super("OpenGLCameraManagerThread");
            this.mCallback = callback;
        }

        public void inputContactList(List<UploadContactBean> contactList) {

            for (int i = 0; i < contactList.size(); i++) {
                addContact(contactList.get(i));
                if (mCallback != null) {
                    mCallback.onProgress(i + 1 / contactList.size());
                }
                if (i == contactList.size() - 1) {
                    if (mCallback != null) {
                        mCallback.onFinish();
                    }
                }
            }
        }

        /**
         * 添加联系人到本机
         */
        public static boolean addContact(UploadContactBean uploadContactBean) {
            try {
                ContentValues values = new ContentValues();

                // 下面的操作会根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContactId
                Uri rawContactUri = CCApplication.getInstance().getContentResolver().insert(
                        ContactsContract.RawContacts.CONTENT_URI, values);
                long rawContactId = ContentUris.parseId(rawContactUri);

                // 向data表插入姓名数据
                String name = uploadContactBean.getName();
                if (!TextUtils.isEmpty(name)) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
                    CCApplication.getInstance().getContentResolver().insert(
                            ContactsContract.Data.CONTENT_URI, values);
                }

                // 向data表插入电话数据
                String mobile_number = uploadContactBean.getPhoneNumber();
                if (!TextUtils.isEmpty(mobile_number)) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile_number);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    CCApplication.getInstance().getContentResolver().insert(
                            ContactsContract.Data.CONTENT_URI, values);
                }

                // 向data表插入Email数据
                String email = uploadContactBean.getEmail();
                if (!TextUtils.isEmpty(email)) {
                    values.clear();
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.Email.DATA, email);
                    values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                    CCApplication.getInstance().getContentResolver().insert(
                            ContactsContract.Data.CONTENT_URI, values);
                }

            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}
