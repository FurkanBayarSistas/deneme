package com.audiocodes.mv.webrtcclient.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;

public class CallForegroundService extends MediaSessionService {



    private static final String TAG = "CallForeService";

    private static final String START_FOREGROUND = "START_FOREGROUND";
    private static final String STOP_FOREGROUND = "STOP_FOREGROUND";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "Received intent: " + intent.getAction());
            String action = intent.getAction();
            if (START_FOREGROUND.equals(action)) {
                Log.d(TAG, "Received Start Foreground Intent ");

            } else if (STOP_FOREGROUND.equals(action)) {
                Log.d(TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    public static void startService(){
        Log.d(TAG, "startService foreground ");
        Context context = MainApp.getGlobalContext();
        Intent startIntent = new Intent(context, CallForegroundService.class);
        startIntent.setAction(CallForegroundService.START_FOREGROUND);
        context.startService(startIntent);
    }

    public static void stopService(){
        Log.d(TAG, "stopService foreground ");
        Context context = MainApp.getGlobalContext();
        Intent stopIntent = new Intent(context, CallForegroundService.class);
        stopIntent.setAction(CallForegroundService.STOP_FOREGROUND);
        context.stopService(stopIntent);
    }

}