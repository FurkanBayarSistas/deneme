package com.audiocodes.mv.webrtcclient.General;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import androidx.multidex.MultiDexApplication;

import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcclient.Receivers.EventReceiver;
import com.audiocodes.mv.webrtcclient.db.MySQLiteHelper;

import java.io.File;

public class MainApp extends MultiDexApplication {

    private static final String TAG = "MainApp";

    private static Context globalContext;
    private static Activity currentActivity;
    private static Activity previousActivity;

    private static MySQLiteHelper dataBase;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Start Application");
        globalContext = this;
        setLogLevel();
        startRecivers();
        initRingbackFile();

    }

    private void setLogLevel() {
        Log.LogLevel logLevel = Prefs.getLogLevel();
        Log.setLogLevel(logLevel);
    }

    public static Context getGlobalContext() {
        return globalContext;
    }

    private void startRecivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new EventReceiver(), intentFilter);
    }

    private void initRingbackFile(){
        String fileName = "ringback.wav";
        File file = globalContext.getFileStreamPath(fileName);
        if(file.exists()){
            file.delete();
        }
        AppUtils.copyFileFromRawToData(globalContext, fileName, R.raw.ringback);
    }

    public static void initDataBase()
    {
        dataBase = new MySQLiteHelper(globalContext);
    }

    public static MySQLiteHelper getDataBase() {
        return dataBase;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        Log.d(TAG , "setCurrentActivity: " +activity);
        if(currentActivity!=null && currentActivity != getPreviousActivity() && currentActivity != activity){
            setPriviousActivity(currentActivity);
        }
        currentActivity = activity;
    }

    public static Activity getPreviousActivity() {
        return previousActivity;
    }

    public static void setPriviousActivity(Activity activity) {
        previousActivity = activity;
    }
}
