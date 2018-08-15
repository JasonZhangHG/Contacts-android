package cool.contacts.android.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import cool.contacts.android.base.CCApplication;

public class PermissionUtil {

    public static boolean checkSinglePermission(String permission){
        return CCApplication.getInstance() != null && ContextCompat.checkSelfPermission(CCApplication.getInstance(),permission) == PackageManager.PERMISSION_GRANTED;
    }

    //Camera 权限
    public static boolean hasCameraPermission() {
        return checkSinglePermission(Manifest.permission.CAMERA);
    }

    //麦克风权限
    public static boolean hasRecordAudioPermission() {
        return checkSinglePermission(Manifest.permission.RECORD_AUDIO);
    }

    public static boolean hasPhonePermission() {
        return checkSinglePermission(Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean hasStoragePermission() {
        return checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean hasContactPermission() {
        return checkSinglePermission(Manifest.permission.READ_CONTACTS);
    }

    public static boolean hasNoLocationPermission() {
        return !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean hasLocationPermission() {
        return checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) && checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
