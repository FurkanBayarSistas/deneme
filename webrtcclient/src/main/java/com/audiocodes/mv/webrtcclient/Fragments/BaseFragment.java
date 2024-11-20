package com.audiocodes.mv.webrtcclient.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.audiocodes.mv.webrtcclient.Login.LogoutManager;
import com.audiocodes.mv.webrtcclient.R;

public class BaseFragment extends Fragment {
    public Handler handler;

    private final String TAG = "BaseFragment";

    private static final int SETTING_MENU = 100;
    private static final int EXIT_MENU = 101;


    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setMenuVisibility(true);
        setHasOptionsMenu(true);
        handler = new Handler();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear current contents
        menu.clear();
        menu.add(Menu.NONE, SETTING_MENU, Menu.NONE, getString(R.string.menu_option_settings));
        menu.add(Menu.NONE, EXIT_MENU, Menu.NONE, getString(R.string.menu_option_exit));
        super.onCreateOptionsMenu(menu, inflater);
    }
}
