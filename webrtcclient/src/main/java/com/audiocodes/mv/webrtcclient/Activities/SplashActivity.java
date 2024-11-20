package com.audiocodes.mv.webrtcclient.Activities;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.audiocodes.mv.webrtcclient.General.ACManager;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;
import com.audiocodes.mv.webrtcclient.General.Prefs;
import com.audiocodes.mv.webrtcclient.Login.LoginManager;
import com.audiocodes.mv.webrtcclient.Login.LoginManager.AppLoginState;
import com.audiocodes.mv.oauth.OAuthManager;
import com.audiocodes.mv.webrtcclient.Permissions.PermissionManager;
import com.audiocodes.mv.webrtcclient.Permissions.PermissionRequest;
import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcclient.Structure.SipAccount;
import com.audiocodes.mv.webrtcsdk.sip.enums.Transport;
import com.audiocodes.mv.webrtcsdk.useragent.AudioCodesUA;


public class SplashActivity extends BaseAppCompatActivity {

    private static final String TAG = "SplashActivity";

    private static final int delayStartupInterval = 500;

    private static boolean isPermissionRequestActive;
    private static boolean allPermissionsGranted = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hasToolbar = false;
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        appStateCheck();
        setContentView(R.layout.splash_activity);
        appPermissionCheck();
    }

    private void appStateCheck() {
        if (LoginManager.getAppState() == AppLoginState.CRUSHED) {
            //silent login


        } else if (LoginManager.getAppState() == AppLoginState.ACTIVE) {
            //skip splash screen
            startApp();
        }
    }

    private void appPermissionCheck() {

        Log.d(TAG, "Check requestAllPermissions");
        isPermissionRequestActive = true;
        PermissionRequest permissionRequest = new PermissionRequest() {
            @Override
            public void granted() {
                Log.d(TAG, " PermissionRequest: granted");
//                initStartApp();
            }


            @Override
            public void revoked() {
                //Can close app if not all permission is approved
                Log.d(TAG, " PermissionRequest: revoked");
            }

            @Override
            public void allResults(boolean allGranted) {
                Log.d(TAG, " allGranted: " + allGranted);

                allPermissionsGranted = allGranted;
                initStartApp();
            }
        };
        PermissionManager.getInstance().requestAllPermissions(SplashActivity.this, permissionRequest);

    }
//push deneme
    private void initStartApp() {
        isPermissionRequestActive = false;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (LoginManager.getAppState() == AppLoginState.CLOSED) {
            //for full login
            new DelayStartApp().execute();
        } else {
            //for quick login
            startApp();
        }
    }

    private void startApp() {
        OAuthManager.getInstance().initialize(MainApp.getGlobalContext(), new OAuthManager.EventsCallback() {
            @Override
            public void onRelogin() {
                android.util.Log.d(TAG, "relogin");
                Prefs.setFirstLogin(true);
                MainApp.getCurrentActivity().finish();
            }

            @Override
            public void onUpdateToken(String token) {
                AudioCodesUA.getInstance().setOauthToken(token);
            }
        });

        Log.d(TAG, "startApp");
        boolean isFirstLogin = Prefs.isFirstLogin();
        Log.d(TAG, "isFirstLogin: " + isFirstLogin);


        Prefs.setUsePush(true);
        //check if all permission are granted
        if (allPermissionsGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (isFirstLogin) {
                loginMethod();
                finish();
                return;
            }
            //start login and open app main screen
            ACManager.getInstance().startLogin();
            startNextActivity(MainActivity.class);
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionManager.REQUEST_CODE_ASK_PERMISSIONS) {
            int grantedRes = 0;
            for (int i = 0; i < permissions.length; i++) {
                String permision = permissions[i];
                int grantResult = grantResults[i];
                boolean isLastPermission = i == (permissions.length - 1);

                Log.d(TAG, "onRequestPermissionsResult: permision: " + permision);
                Log.d(TAG, "onRequestPermissionsResult: grantResult: " + grantResult);
                Log.d(TAG, "isLastPermission: " + isLastPermission);

                //calculate results results
                grantedRes += grantResult;

                switch (grantResult) {
                    case PackageManager.PERMISSION_GRANTED:
                        PermissionManager.getInstance().getPermissionRequestList().get(i).granted();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        PermissionManager.getInstance().getPermissionRequestList().get(i).revoked();
                        break;

                }
            }

            if (permissions.length > 0) {
                PermissionManager.getInstance().getPermissionRequestList().get(permissions.length - 1).allResults(grantedRes == 0);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (isPermissionRequestActive) {
            finish();
        }
    }

    class DelayStartApp extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            startApp();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                synchronized (this) {
                    // Wait given period of time or exit on touch
                    Thread.sleep(delayStartupInterval);
                }
            } catch (InterruptedException ex) {
            }
            return null;
        }
    }

    private void loginMethod()
    {
        SipAccount sipAccount = new SipAccount();
        String userName = "123456789";
        String password = "0000";

        sipAccount.setUsername("123456789");
        sipAccount.setPassword("0000");
        sipAccount.setDisplayName("Test");
        sipAccount.setDomain("demo.sistas.com.tr");
        sipAccount.setProxy("demo.sistas.com.tr");
        sipAccount.setTransport(Transport.TLS);
        sipAccount.setPort(10080);
        Prefs.setSipAccount(sipAccount);
        openNextScreen(false, false);
    }

    private void openNextScreen(boolean autologin, boolean disconnectCall)
    {
        Prefs.setFirstLogin(false);
        //start login and open app main screen
        ACManager.getInstance().startLogin(autologin, disconnectCall);
        startNextActivity(MainActivity.class);
    }
}


