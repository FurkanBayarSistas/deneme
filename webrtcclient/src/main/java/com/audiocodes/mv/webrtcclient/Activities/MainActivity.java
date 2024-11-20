package com.audiocodes.mv.webrtcclient.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import com.audiocodes.mv.webrtcclient.Adapters.ViewPagerAdapter;
import com.audiocodes.mv.webrtcclient.Callbacks.CallBackHandler;
import com.audiocodes.mv.webrtcclient.Callbacks.CallBackHandler.LoginStateChanged;
import com.audiocodes.mv.webrtcclient.Fragments.DialerFragment;
import com.audiocodes.mv.webrtcclient.Fragments.FragmentLifecycle;
import com.audiocodes.mv.webrtcclient.General.AppUtils;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;
import com.audiocodes.mv.webrtcclient.Login.LoginManager;
import com.audiocodes.mv.webrtcclient.R;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends BaseAppCompatActivity {


    private ViewPagerAdapter adapter;
    private ViewPager viewPager;

    private static final String TAG = "MainActivity";

    LoginStateChanged loginStateChanged = new LoginStateChanged() {
        @Override
        public void loginStateChange(boolean state) {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainApp.getGlobalContext(), "login state: "+state, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"login state: "+state);
                    }
                });
            }
        }
    };

    CallBackHandler.TabChangeCallback tabChangeCallback = new CallBackHandler.TabChangeCallback() {
        @Override
        public void onTabChange(int tabIndex) {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                      //  adapter.setPrimaryItem(tabIndex);
                        viewPager.setCurrentItem(tabIndex, true);
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int displaymode = AppUtils.checkOrientation(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_activity);

        initRotationMode(displaymode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int displaymode = AppUtils.checkOrientation(this);
        super.onConfigurationChanged(newConfig);
        initRotationMode(displaymode);
    }

    //
    private void initRotationMode(int displaymode)
    {
        Log.d(TAG, "get orientation: "+displaymode);
        if (displaymode == 1) { // its portrait mode
            setContentView(R.layout.main_activity);
            LoginManager.setAppState(LoginManager.AppLoginState.ACTIVE);
            MainApp.initDataBase();
            initTabs();
        } else {// its landscape
            setContentView(R.layout.main_fragments);
            LoginManager.setAppState(LoginManager.AppLoginState.ACTIVE);
            MainApp.initDataBase();
            initFragments();
        }


        //initRotationMode();
        CallBackHandler.registerLginStateChange(loginStateChanged);
    }

    private void initFragments()
    {
        initTabs();
    }

    private void initTabs()
    {
        CallBackHandler.registerTabChangeCallback(tabChangeCallback);

        viewPager = (ViewPager)findViewById(R.id.main_activity_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DialerFragment(), getString(R.string.tabs_dialer_title));

        viewPager.setAdapter(adapter);

        //For refreshing data when returning to fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                FragmentLifecycle fragmentToHide = (FragmentLifecycle) adapter.getItem(currentPosition);
                fragmentToHide.onPauseFragment();

                FragmentLifecycle fragmentToShow = (FragmentLifecycle) adapter.getItem(position);
                fragmentToShow.onResumeFragment();

                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    protected void onDestroy() {
        CallBackHandler.unregisterLoginStateChange(loginStateChanged);
        CallBackHandler.unregisterTabChangeCallback(tabChangeCallback);
        super.onDestroy();
    }
}
