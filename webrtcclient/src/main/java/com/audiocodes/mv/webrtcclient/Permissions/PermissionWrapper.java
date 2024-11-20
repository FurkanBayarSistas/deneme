package com.audiocodes.mv.webrtcclient.Permissions;


import android.Manifest;

public class PermissionWrapper
{
    private static String TAG = "PermissionWrapper";

    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";
    public static final String CAMERA = "android.permission.CAMERA";
  //  public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    public static final String READ_SMS = "android.permission.READ_SMS";
    public static final String READ_MEDIA_AUDIO = getAudioPermission();

    public static final String BLUETOOTH = getBluetoothPermission();// "android.permission.BLUETOOTH_CONNECT";

    public static final String NOTIFICATION = getNotificationPermission();// "android.permission.BLUETOOTH_CONNECT";

    private static PermissionWrapper instance;
    private IPermissionManager iPermissionManager;

    private static String getAudioPermission()
    {
        String tmp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            tmp = Manifest.permission.READ_MEDIA_AUDIO;
        }
        return tmp;
    }
    private static String getNotificationPermission()
    {
        String tmp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            tmp = Manifest.permission.POST_NOTIFICATIONS;
        }
        return tmp;
    }

    private static String getBluetoothPermission()
    {
        String tmp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            tmp = Manifest.permission.BLUETOOTH_CONNECT;
        }

        return tmp;
    }

    public static PermissionWrapper getInstance()
    {
        if (instance == null)
        {
            instance = new PermissionWrapper();
        }
        return instance;
    }

    public void setiPermissionManager(IPermissionManager iPermissionManager)
    {
        this.iPermissionManager = iPermissionManager;
    }

    public boolean checkPermission(PermissionManagerType permissionType)
    {
        if(iPermissionManager == null)
        {
            //we don't have implemntetion for iPermissionManager return true
            return true;
        }
        return iPermissionManager.checkPermission(permissionType);
    }
}
