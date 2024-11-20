package com.audiocodes.mv.webrtcclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.audiocodes.mv.webrtcclient.General.AppUtils;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;

public class BaseAppCompatActivity extends AppCompatActivity {

    public Handler handler;
    public boolean hasToolbar=true;
    private Toolbar toolbar;
    public View layoutView;
    private static final String TAG = "BaseAppCompatActivity";


    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.checkOrientation(this);
        super.onCreate(savedInstanceState);
        MainApp.setCurrentActivity(this);
        handler = new Handler();
    }

    public void startNextActivity(Class<?> cls) {
        Intent intent = new Intent(BaseAppCompatActivity.this, cls);
        startActivity(intent);
        finish();
    }

    @Override
    public void setContentView(int layoutResID) {
        layoutView = View.inflate(this, layoutResID, null);
        setContentView(layoutView);
    }

    @Override
    public void setContentView(View view) {
        layoutView = view;
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        layoutView = view;
        super.setContentView(view, params);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(hasToolbar) {
            Log.d(TAG, "onPrepareOptionsMenu: " + menu.hasVisibleItems());
            Log.d(TAG, "onPrepareOptionsMenu: " + menu.size());
            adjustTitleBar(menu.size() > 0);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void adjustTitleBar(boolean hasMenu) {
        Log.d(TAG, "adjustTitleBar, hasMenu: " + hasMenu);

        // menu button
        int layoutMarginLeft=0;//value in dp - this is the size of the menu icon

        Log.d(TAG, "toolBar.getPaddingLeft(); " + toolbar.getPaddingLeft());

        if (hasMenu) {
            int paddingLeft =toolbar.getPaddingLeft();
            if (paddingLeft != 12) {
                layoutMarginLeft = 40;//value in dp - this is the size of the menu icon
            }
            else
            {
                layoutMarginLeft = 36;
            }
        }
        Log.d(TAG, "layoutMarginLeft: "  + layoutMarginLeft);
        int layoutMarginLeftInPixel= (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, layoutMarginLeft, getResources()
                        .getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApp.setCurrentActivity(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApp.setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApp.setCurrentActivity(this);
    }
}
