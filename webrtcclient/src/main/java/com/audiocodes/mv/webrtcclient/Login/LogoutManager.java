package com.audiocodes.mv.webrtcclient.Login;


import com.audiocodes.mv.webrtcclient.Callbacks.CallBackHandler;
import com.audiocodes.mv.webrtcclient.General.ACManager;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;

public class LogoutManager {

    private static final String TAG = "LogoutManager";


    private static int LOGOUT_TIMEOUT_INTERVAL = 1;
    private static Thread closeAppThread;

    public static void closeApplication()
    {
        Log.d(TAG, "closeApplication");
        Log.d(TAG, "close GUI");

        LoginManager.setAppState(LoginManager.AppLoginState.CLOSED);

        if(MainApp.getCurrentActivity()!=null)
        {
            Log.d(TAG, "close Activity");
            MainApp.getCurrentActivity().finish();;
        }
        else
        {
            if(MainApp.getPreviousActivity()!=null)
            {
                Log.d(TAG, "close prev Activity");
                MainApp.getPreviousActivity().finish();;
            }
        }

        if (ACManager.getInstance().isRegisterState())
        {
            Log.d(TAG, "Unregister client");
            ACManager.getInstance().startLogout();
        }


        closeAppThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(LOGOUT_TIMEOUT_INTERVAL);
                } catch (InterruptedException e) {
                    Log.d(TAG,"closeAppThread interapted");
                }
                endCloseApplication();

            }
        });
        closeAppThread.start();
    }

    CallBackHandler.LoginStateChanged loginStateChanged = new CallBackHandler.LoginStateChanged() {
        @Override
        public void loginStateChange(boolean state) {
            if(!state) {
                if (closeAppThread != null) {
                    closeAppThread.interrupt();
                } else {
                    endCloseApplication();
                }
            }
        }
    };

    public static void endCloseApplication()
    {
        Log.d(TAG, "close Process");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
