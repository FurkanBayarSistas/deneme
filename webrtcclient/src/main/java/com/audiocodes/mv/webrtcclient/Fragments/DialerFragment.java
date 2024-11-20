package com.audiocodes.mv.webrtcclient.Fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.audiocodes.mv.webrtcclient.General.ACManager;
import com.audiocodes.mv.webrtcclient.General.AppUtils;
import com.audiocodes.mv.webrtcclient.General.MainApp;
import com.audiocodes.mv.webrtcclient.General.Prefs;
import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcclient.Structure.CallEntry;

import java.util.List;


public class DialerFragment extends BaseFragment implements FragmentLifecycle {

    private static final String TAG = "DialerFragment";

    private static final int EXIT_MENU = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.main_fragment_dialer, container, false);

        initGui(rootView);

        return rootView;
    }

    private void initGui(View rootView) {
        int[] keypadButtonClickListID = {R.id.dialer_button_video_call};

        View.OnClickListener dialpadClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                String currentNumber = "";

                if (clickedView == null) {
                    return;
                }
                boolean videoCall = false;
                int id = clickedView.getId();

                if (id == R.id.dialer_button_video_call) {
                    videoCall=true;
                }
                if (id == R.id.dialer_button_video_call) {

                        if (!ACManager.getInstance().isRegisterState() && Prefs.getAutoLogin())
                        {
                            Toast.makeText(getContext(), R.string.no_registration, Toast.LENGTH_SHORT).show();;
                        }
                        else {
                            ACManager.getInstance().callNumber("79005864", videoCall);
                        }
                        return;
                }
            }
        };
        for (int keypadButtonClickID : keypadButtonClickListID) {
            View view = rootView.findViewById(keypadButtonClickID);
            if (view != null) {
                view.setOnClickListener(dialpadClickListener);
            }
        }
    }

    private void updateCallNumber(EditText callNumberEditText, String newString) {
        if (callNumberEditText != null && newString != null) {
            int focusLocation = callNumberEditText.getSelectionStart();
            callNumberEditText.setText(callNumberEditText.getText().append(newString));
            callNumberEditText.setSelection(focusLocation + newString.length());
        }
    }

    @Override
    public void onPauseFragment() {
    }

    @Override
    public void onResumeFragment() {
    }
}