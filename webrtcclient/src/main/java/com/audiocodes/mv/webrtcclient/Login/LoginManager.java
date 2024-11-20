package com.audiocodes.mv.webrtcclient.Login;


import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.Prefs;

public class LoginManager {

    private static final String TAG = "LoginManager";

    private static boolean isAppOpen = false;


    public enum AppLoginState
    {
        CLOSED,
        CRUSHED,
        LOGOUT,
        ACTIVE
    }

    /**
     * @return the state the app is in CLOSED,CRUSHED,LOGOUT,ACTIVE
     */
    public static AppLoginState getAppState()
    {
        AppLoginState appLoginState = AppLoginState.ACTIVE;
        if (!Prefs.isAppOpen() && !isAppOpen)
        {
            appLoginState = AppLoginState.CLOSED;
        }
        if (Prefs.isAppOpen() && !isAppOpen)
        {
            appLoginState = AppLoginState.CRUSHED;
        }
        if (!Prefs.isAppOpen() && isAppOpen)
        {
            appLoginState = AppLoginState.LOGOUT;
        }
        Log.d(TAG, "App state: " + appLoginState);
        return appLoginState;

    }

    /**
     * set the app state (CLOSED,CRUSHED,LOGOUT,ACTIVE)
     */
    public static void setAppState(AppLoginState appLoginState)
    {
        // Prefs.isAppOpen() = is app open prefs
        // isAppOpen = is app open static value
        switch (appLoginState)
        {
            case ACTIVE:
                isAppOpen = true;
                Prefs.setAppOpen(true);
                break;
            case CLOSED:
                isAppOpen = false;
                Prefs.setAppOpen(false);
                break;
            case CRUSHED:
                isAppOpen = false;
                Prefs.setAppOpen(true);
                break;
            case LOGOUT:
                isAppOpen = true;
                Prefs.setAppOpen(false);
                break;
        }
    }

}
