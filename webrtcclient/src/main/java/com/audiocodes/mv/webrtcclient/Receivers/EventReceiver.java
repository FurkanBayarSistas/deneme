package com.audiocodes.mv.webrtcclient.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.audiocodes.mv.webrtcclient.General.ACManager;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.Prefs;
import com.audiocodes.mv.webrtcsdk.useragent.AudioCodesUA;

public class EventReceiver extends BroadcastReceiver {
    static final String TAG = "EventsReceiver";
    private static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static boolean connected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);

        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        Log.d(TAG, "Internet is connected: " + networkInfo.isConnected());
        if (!networkInfo.isConnected() ) {
            ACManager.getInstance().loginStateChanged(false,0,"CONNECTIVITY_CHANGE");

        }
        if (networkInfo.isConnected() ) {
            AudioCodesUA.getInstance().handleNetworkChange();
        }
        connected = networkInfo.isConnected();
    }
}
