package com.audiocodes.mv.webrtcclient.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.audiocodes.mv.webrtcclient.Callbacks.CallBackHandler;
import com.audiocodes.mv.webrtcclient.General.ACManager;
import com.audiocodes.mv.webrtcclient.General.AppUtils;
import com.audiocodes.mv.webrtcclient.General.ImageUtils;
import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.MainApp;
import com.audiocodes.mv.webrtcclient.General.Prefs;
import com.audiocodes.mv.webrtcclient.Login.LogoutManager;
import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcclient.db.NativeDBManager;
import com.audiocodes.mv.webrtcclient.db.NativeDBObject;
import com.audiocodes.mv.webrtcclient.services.CallForegroundService;
import com.audiocodes.mv.webrtcsdk.audio.WebRTCAudioManager;
import com.audiocodes.mv.webrtcsdk.audio.WebRTCAudioRouteListener;
import com.audiocodes.mv.webrtcsdk.session.AudioCodesSession;
import com.audiocodes.mv.webrtcsdk.session.AudioCodesSessionEventListener;
import com.audiocodes.mv.webrtcsdk.session.CallState;
import com.audiocodes.mv.webrtcsdk.session.CallTermination;
import com.audiocodes.mv.webrtcsdk.session.NotifyEvent;
import com.audiocodes.mv.webrtcsdk.session.TerminationInfo;
import com.audiocodes.mv.webrtcsdk.session.CallTransferState;
import com.audiocodes.mv.webrtcsdk.session.DTMF;
import com.audiocodes.mv.webrtcsdk.session.RemoteContact;
import com.audiocodes.mv.webrtcsdk.useragent.ACConfiguration;
import com.audiocodes.mv.webrtcsdk.useragent.AudioCodesUA;
import com.audiocodes.mv.webrtcsdk.useragent.WebRTCException;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends BaseAppCompatActivity implements AudioCodesSessionEventListener {

    private static final String TAG = "CallActivity";
    public static final String SESSION_ID = "sessionID";


    // private Handler handler;

    private TextView callStateTextView;
    private TextView transferStateNumberTextView;

    private TextView contactNameTextView;
    private TextView contactPhoneNumberTextView;
    private ImageView contactImageView;

    private AudioCodesSession session;

    private View endCallButton;
    private View holdButton;
    private View switchCameraButton;
    private View muteAudioButton;
    private View switchCallButton;
    private View addCallButton;
    private View transferCallButton;
    private View audioRouteButton;
    private View addVideoButton;
    private View dtmfButton;

    private boolean dtmfEnabled = false;
    private boolean guiInitialized;

    private WebRTCAudioManager ac;
    private WebRTCAudioManager.AudioRoute route = WebRTCAudioManager.AudioRoute.EARPIECE;

    private ArrayList<Integer> sessionList = new ArrayList();

    private MyAudioCodesSessionEventListener myAudioCodesSessionEventListener = new MyAudioCodesSessionEventListener();

    private int lastSessionIndex;

    CallBackHandler.LoginStateChanged loginStateChanged = new CallBackHandler.LoginStateChanged() {
        @Override
        public void loginStateChange(boolean state) {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        endCallButton.setEnabled(false);
                        holdButton.setEnabled(false);
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.call_activity);
        handler = new Handler();
        CallBackHandler.registerLginStateChange(loginStateChanged);
        ac = WebRTCAudioManager.getInstance();

        Integer sessionIndex = this.getIntent().getIntExtra(SESSION_ID, -1);
        if (sessionIndex == -1) {
            Log.d(TAG, "getActiveSession: " + ACManager.getInstance().getActiveSession());
            Log.d(TAG, "getSessionList: " + ACManager.getInstance().getSessionList());

            if (ACManager.getInstance().getActiveSession() != null) {
                sessionIndex = ACManager.getInstance().getActiveSession().getSessionID();
            } else {
                //close application
                LogoutManager.closeApplication();
                return;
            }
        }

        Log.d(TAG, "Session: " + sessionIndex);
        if (!sessionList.contains(sessionIndex)) {
            sessionList.add(sessionIndex);
        }

        session = AudioCodesUA.getInstance().getSession(sessionIndex);
        if(session == null){
            Log.d(TAG, "Session is null!!!");
            Toast.makeText(MainApp.getGlobalContext(),getString(R.string.session_null_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        lastSessionIndex = sessionIndex;
        updateUI();
        CallForegroundService.startService();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");

        int sessionIndex = intent.getIntExtra(SESSION_ID, -1);
        if(sessionIndex == -1){
            sessionIndex = lastSessionIndex;
        }
        else{
            lastSessionIndex = sessionIndex;
        }
        Log.d(TAG, "Session: " + sessionIndex);
        if (!sessionList.contains(sessionIndex)) {
            sessionList.add(sessionIndex);
        }
        if (session.hasVideo()) {
            session.stopVideo();
        }
        session = AudioCodesUA.getInstance().getSession(sessionIndex);
        switchCallButton.setEnabled(true);
        updateUI();
    }

    private void initGUI() {

        callStateTextView = (TextView) findViewById(R.id.call_textview_call_state);
        transferStateNumberTextView = (TextView) findViewById(R.id.call_textview_call_transfer_state_number);

        contactNameTextView = (TextView) findViewById(R.id.call_textview_contact_name);
        contactPhoneNumberTextView = (TextView) findViewById(R.id.call_textview_contact_number);
        contactImageView = (ImageView) findViewById(R.id.call_imageView_contact_image);

        endCallButton = findViewById(R.id.call_button_end_call);
        dtmfButton = findViewById(R.id.call_button_dtmf);
        audioRouteButton = findViewById(R.id.call_button_audio_route);
        addVideoButton = findViewById(R.id.call_button_add_video);

        addCallButton = findViewById(R.id.call_button_add_call);
        switchCallButton = findViewById(R.id.call_button_switch_call);
        muteAudioButton = findViewById(R.id.call_button_mute);
        holdButton = findViewById(R.id.call_button_hold);
        switchCameraButton = findViewById(R.id.call_button_switch_camera);
        transferCallButton = findViewById(R.id.call_button_transfer);

        transferCallButton.setEnabled(true);

        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "session getTermination: " + session.getTermination());
                if (session.getTermination() == CallTermination.TERMINATED_NOT_FOUND) {
                    handleCallTermination(session,null);
                    return;
                }
                if (session.getTermination() == CallTermination.TERMINATED_MEDIA_FAILED) {
                    handleCallTermination(session,null);
                    return;
                }
                //session.
                Log.d(TAG, "session getCallState: " + session.getCallState());
                Log.d(TAG, "session getTermination: " + session.getTermination());
                session.terminate();
                handleCallTermination(session,null);
            }
        });

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.switchCamera();
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLocalHold = session.isLocalHold();
                setHold(!isLocalHold);
            }
        });

        muteAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isAudioMuted = session.isAudioMuted();
                Log.d(TAG, "isAudioMuted before: " + isAudioMuted);

                isAudioMuted = !isAudioMuted;

                session.muteAudio(isAudioMuted);
                session.muteVideo(isAudioMuted);

                isAudioMuted = session.isAudioMuted();
                Log.d(TAG, "isAudioMuted after: " + isAudioMuted);
                muteAudioButton.setSelected(isAudioMuted);
            }
        });

        switchCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Integer index : sessionList) {
                    Log.d(TAG, "Session ID: " + index);
                }
                for (Integer index : sessionList) {

                    if (index != session.getSessionID()) {
                        session.hold(true);
                        session = AudioCodesUA.getInstance().getSession(index);
                        session.hold(false);
                        updateUI();
                        Log.d(TAG, "Session index: " + index);
                        return;
                    }
                }

            }
        });


        addCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String call = Prefs.getSecondCall();
                if(call == null || call.equals("")){
                    Toast.makeText(MainApp.getGlobalContext(),getString(R.string.no_remote_contact), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Log.d(TAG, "Calling to: " + call);
                    if (session.hasVideo()) {
                        session.stopVideo();
                    }
                    RemoteContact contact = new RemoteContact();
                    contact.setUserName(call);
                    session = AudioCodesUA.getInstance().call(contact, session.hasVideo(), null);
                    Log.d(TAG, "Added call_activity: " + session.getSessionID());
                    if (!sessionList.contains(session.getSessionID())) {
                        sessionList.add(session.getSessionID());
                    }
                    switchCallButton.setEnabled(true);
                    updateCallGui();
                    updateUI();
                } catch (WebRTCException e) {
                    Log.d(TAG, "oops: " + e.getMessage());
                }
            }
        });

        transferCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionList.size() > 1)
                {
                    for (Integer index : sessionList) {
                        if (index != session.getSessionID()) {
                            AudioCodesSession transferSession = AudioCodesUA.getInstance().getSession(index);
                            transferSession.transferCall(session);
                            updateUI();
                            return;
                        }
                    }
                }
                else
                {
                    String call = Prefs.getTransferCall();
                    if(call == null || call.equals("")){
                        Toast.makeText(MainApp.getGlobalContext(),getString(R.string.no_remote_contact), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RemoteContact transferContact = new RemoteContact();
                    transferContact.setUserName(call);
                    session.transferCall(transferContact);
                }
            }
        });

        audioRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (route == WebRTCAudioManager.AudioRoute.BLUETOOTH) {
                    route = WebRTCAudioManager.AudioRoute.SPEAKER_PHONE;
                } else if (route == WebRTCAudioManager.AudioRoute.SPEAKER_PHONE) {
                    route = WebRTCAudioManager.AudioRoute.EARPIECE;
                } else {
                    route = WebRTCAudioManager.AudioRoute.BLUETOOTH;
                }
                Log.d("WebRTCAudioManager", "new route = " + route);
                ac.setAudioRoute(route);
            }
        });

        addVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "hasVideo: " + session.hasVideo() + " isVideoMuted: " + session.isVideoMuted());
                if (session.hasVideo()) {
                    Log.d(TAG, "new video mute stat: " + !session.isVideoMuted());
                    session.muteVideo(!session.isVideoMuted());
                } else {
                    Log.d(TAG, "add video to the session");
                    session.reinviteWithVideo();
                }
                updateVideoButton();
            }
        });

        dtmfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtmfEnabled = !dtmfEnabled;
                //session.sendDTMF(dtmf);
                updateCallGui();
            }
        });
        session.addSessionEventListener(this);

        initDtmf();
        updateVideoButton();
        guiInitialized = true;
    }

    private void setHold(boolean hold)
    {
        Log.d(TAG, "setHold: " + hold);
        holdButton.setSelected(hold);
        session.hold(hold);
    }
    private void initDtmf() {
        int[] keypadButtonClickListID = {R.id.call_button_keypad_1, R.id.call_button_keypad_2, R.id.call_button_keypad_3,
                R.id.call_button_keypad_4, R.id.call_button_keypad_5, R.id.call_button_keypad_6,
                R.id.call_button_keypad_7, R.id.call_button_keypad_8, R.id.call_button_keypad_9,
                R.id.call_button_keypad_hash, R.id.call_button_keypad_0, R.id.call_button_keypad_asterisk};

        View.OnClickListener dialpadClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                if (clickedView == null) {
                    return;
                }
                int itemId = clickedView.getId();
                if (itemId ==R.id.call_button_keypad_1 )
                        session.sendDTMF(DTMF.ONE);
                else if (itemId ==R.id.call_button_keypad_2 )
                    session.sendDTMF(DTMF.TWO);
                else if (itemId ==R.id.call_button_keypad_3 )
                    session.sendDTMF(DTMF.THREE);
                else if (itemId ==R.id.call_button_keypad_4 )
                    session.sendDTMF(DTMF.FOUR);
                else if (itemId ==R.id.call_button_keypad_5 )
                    session.sendDTMF(DTMF.FIVE);
                else if (itemId ==R.id.call_button_keypad_6 )
                    session.sendDTMF(DTMF.SIX);
                else if (itemId ==R.id.call_button_keypad_7 )
                    session.sendDTMF(DTMF.SEVEN);
                else if (itemId ==R.id.call_button_keypad_8 )
                    session.sendDTMF(DTMF.EIGHT);
                else if (itemId ==R.id.call_button_keypad_9 )
                    session.sendDTMF(DTMF.NINE);
                else if (itemId ==R.id.call_button_keypad_hash )
                    session.sendDTMF(DTMF.POUND);
                else if (itemId ==R.id.call_button_keypad_0 )
                    session.sendDTMF(DTMF.ZERO);
                else if (itemId ==R.id.call_button_keypad_asterisk )
                    session.sendDTMF(DTMF.STAR);
            }
        };

        for (int keypadButtonClickID : keypadButtonClickListID) {
            View view = findViewById(keypadButtonClickID);
            if (view != null) {
                view.setOnClickListener(dialpadClickListener);
            }
        }
    }

    private void updateUI() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!guiInitialized) {
                    initGUI();
                    WebRTCAudioManager.getInstance().setWebRTcAudioRouteListener(new MyWebRTcAudioRouteListener());
                    updateCallGui();

                }
                RemoteContact remote = session.getRemoteNumber();
                if(remote != null){
                    callStateTextView.setText(getString(R.string.call_textview_call_state) + " " + session.getCallState());
                    List<NativeDBObject> nativeDBObjectList = NativeDBManager.getContactList(NativeDBManager.QueryType.BY_PHONE_AND_SIP, remote.getUserName());//  .getContactListByPhoneNumber(session.getRemoteNumber().getUserName());
                    String displayName = remote.getDisplayName();
                    String userName = remote.getUserName();
                    if (nativeDBObjectList != null && nativeDBObjectList.size() > 0) {
                        NativeDBObject nativeDBObject = nativeDBObjectList.get(0);
                        if (nativeDBObject.getPhotoURI() != null) {
                            Bitmap photoBitmap = ImageUtils.getContactBitmapFromURI(MainApp.getGlobalContext(), Uri.parse(nativeDBObject.getPhotoURI()));
                            if (photoBitmap != null) {

                                Bitmap roundPhotoBitmap = ImageUtils.getCroppedRoundBitmap(photoBitmap, contactImageView.getDrawable().getIntrinsicHeight());
                                contactImageView.setImageBitmap(roundPhotoBitmap);
                            }
                        }
                        displayName = nativeDBObject.getDisplayName();
                    }

                    contactNameTextView.setText(displayName);
                    contactPhoneNumberTextView.setText(userName);

                    session.addSessionEventListener(myAudioCodesSessionEventListener);
                }
                else{
                    finish();
                    Log.d(TAG, "RemoteContact null!");
                }
            }
        });

    }

    private void updateVideoButton() {
        int hasVideoInt = 0;
        if (session.hasVideo() && !session.isVideoMuted()) {
            hasVideoInt = R.drawable.call_button_icon_mute_video;
        } else {
            hasVideoInt = R.drawable.call_button_icon_add_video;
        }
        ((ImageView) addVideoButton).setImageResource(hasVideoInt);
    }

    private void updateCallGui() {
        Log.d(TAG, "updateCallGui");
        View videoCallLayout = findViewById(R.id.call_layout_ac_webrtc_video);
        View audioCallLayout = findViewById(R.id.call_layout_audio_call);
        View dtmfCallLayout = findViewById(R.id.call_layout_dtmf_pad);
        View topButtonsLayout = findViewById(R.id.call_layout_top_buttons);
        //View buttonsLayout = findViewById(R.id.call_layout_buttons);


        if (dtmfEnabled) {
            Log.d(TAG, "updateCallGui: DTMF");
            //show dtmf screen
            videoCallLayout.setVisibility(View.GONE);
            audioCallLayout.setVisibility(View.GONE);
            dtmfCallLayout.setVisibility(View.VISIBLE);
            topButtonsLayout.setVisibility(View.GONE);
        } else if (session.hasVideo()) {
            Log.d(TAG, "updateCallGui: video");
            //show video screen
            videoCallLayout.setVisibility(View.VISIBLE);
            audioCallLayout.setVisibility(View.GONE);
            dtmfCallLayout.setVisibility(View.GONE);
            topButtonsLayout.setVisibility(View.VISIBLE);

            try {
                session.setLocaLRenderPosition(70, 50);
                session.showVideo(CallActivity.this);
            } catch (WebRTCException e) {
                Log.d(TAG, "oops: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "updateCallGui: audio");
            //show call screen
            videoCallLayout.setVisibility(View.GONE);
            audioCallLayout.setVisibility(View.VISIBLE);
            dtmfCallLayout.setVisibility(View.GONE);
            topButtonsLayout.setVisibility(View.VISIBLE);

        }
        updateVideoButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session!=null && session.getTermination() != null) {
            handleCallTermination(session,null);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CallBackHandler.unregisterCallStateChanged(callStateChanged);
        CallBackHandler.unregisterLoginStateChange(loginStateChanged);
    }

    @Override
    public void callTerminated(AudioCodesSession session, TerminationInfo terminationInfo) {
        Log.d(TAG, "callTerminated");
        Log.d(TAG, "terminationInfo callTermination: " + terminationInfo.callTermination);
        Log.d(TAG, "terminationInfo statusCode: " + terminationInfo.statusCode);
        Log.d(TAG, "terminationInfo pjssipStatusCode: " + terminationInfo.pjssipStatusCode);
        Log.d(TAG, "terminationInfo reason: " + terminationInfo.reason);
        Log.d(TAG, "terminationInfo reasonHeader: " + terminationInfo.reasonHeader);
        Log.d(TAG, "terminationInfo sipMessage: " + terminationInfo.sipMessage);

    }

    @Override
    public void callProgress(AudioCodesSession session) {
        Log.d(TAG, "callProgress CallState: " + session.getCallState());
        if (session.getCallState() == CallState.CONNECTED || session.getCallState() == CallState.HOLD) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    endCallButton.setEnabled(true);
                    holdButton.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void mediaFailed(AudioCodesSession session) {
       Log.d(TAG, "media failed1");
    }


    @Override
    public void cameraSwitched(boolean frontCamera) {

    }

    @Override
    public void reinviteWithVideoCallback(AudioCodesSession audioCodesSession) {

    }

    @Override
    public void incomingNotify(NotifyEvent notifyEvent, String dtmfValue) {
    }

    public void onNotifyEvent(NotifyEvent notifyEvent, String dtmfValue) {
        Log.d(TAG, "onNotifyEvent: " + notifyEvent);
        switch (notifyEvent) {
            case TALK:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Notify resume");
                        if (session.isLocalHold()) {
                            setHold(false);
                        }
                    }
                });
                break;
            case HOLD:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Notify hold");
                        if (!session.isLocalHold()) {
                            setHold(true);
                        }
                    }
                });
                break;
            case DTMF:
                Log.d(TAG, "Notify DTMF");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DTMF[] dtmfValueArr = getDTMFArrayFromString(dtmfValue);
                        if (dtmfValueArr != null) {
                            for (DTMF dtmf : dtmfValueArr) {
                                if (dtmf != null) {
                                    try {
                                        Thread.sleep(ACConfiguration.getConfiguration().getDtmfOptions().duration + 30);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "incomingNotify dtmfValue: " + dtmf);
                                    session.sendDTMF(dtmf);
                                }
                            }
                        }
                    }
                });
                thread.start();
                break;
        }
    }

    private DTMF[] getDTMFArrayFromString(String dtmfString)
    {
        if (dtmfString==null)
        {
            return null;
        }
        String[] dtmfValueArr = dtmfString.split("");
        DTMF[] res=null;
        if(dtmfValueArr.length>0)
        {
            res = new DTMF[dtmfValueArr.length-1];
            int charIndex= 0;
            for (String signalValueArrChar : dtmfValueArr) {
                if (signalValueArrChar==null || signalValueArrChar.equals(""))
                {
                    continue;
                }
                if (signalValueArrChar.equals("1")) {
                    res[charIndex] = DTMF.ONE;
                } else if (signalValueArrChar.equals("2")) {
                    res[charIndex] = DTMF.TWO;
                } else if (signalValueArrChar.equals("3")) {
                    res[charIndex] = DTMF.THREE;
                } else if (signalValueArrChar.equals("4")) {
                    res[charIndex] = DTMF.FOUR;
                } else if (signalValueArrChar.equals("5")) {
                    res[charIndex] = DTMF.FIVE;
                } else if (signalValueArrChar.equals("6")) {
                    res[charIndex] = DTMF.SIX;
                } else if (signalValueArrChar.equals("7")) {
                    res[charIndex] = DTMF.SEVEN;
                } else if (signalValueArrChar.equals("8")) {
                    res[charIndex] = DTMF.EIGHT;
                } else if (signalValueArrChar.equals("9")) {
                    res[charIndex] = DTMF.NINE;
                } else if (signalValueArrChar.equals("0")) {
                    res[charIndex] = DTMF.ZERO;
                } else if (signalValueArrChar.equals("#")) {
                    res[charIndex] = DTMF.POUND;
                } else if (signalValueArrChar.equals("*")) {
                    res[charIndex] = DTMF.STAR;
                }
                charIndex++;
            }
        }
        return res;
    }

    public class MyAudioCodesSessionEventListener implements AudioCodesSessionEventListener {
        @Override
        public void callTerminated(final AudioCodesSession call, TerminationInfo info) {
            CallForegroundService.stopService();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "MyAudioCodesSessionEventListener");
                    handleCallTermination(call,info);
                }
            });

        }

        @Override
        public void callProgress(final AudioCodesSession call) {
            Log.d(TAG, "Call state: " + call.getCallState());
            Log.d(TAG, "Call session id: " + call.getSessionID());
            Log.d(TAG, "Call number: " + call.getRemoteNumber().getUserName());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(call.getCallState() == CallState.RINGING){
                        Log.d(TAG, "do local ring!");
                        AppUtils.startRingingMP("ringback.wav", true, false, null);
                    }
                    if(call.getCallState() == CallState.CONNECTED){
                        AppUtils.stopRingingMP();
                    }
                    if (call.getSessionID() == session.getSessionID()) {
                        callStateTextView.setText(getString(R.string.call_textview_call_state) + " " + call.getCallState());

                        if (call.getCallState() == CallState.TRANSFER) {
                            if(call.getTransferContact() != null){
                                transferStateNumberTextView.setText(call.getTransferState() + " - " + call.getTransferContact().getUserName());
                            }
                        }
                        else
                        {
                            transferStateNumberTextView.setText("");
                        }
                       // updateUI();
                    }
                    updateUI();
                }
            });
        }

        @Override
        public void cameraSwitched(boolean frontCamera) {
            Log.d(TAG, "cameraSwitched: " + frontCamera);
        }

        @Override
        public void reinviteWithVideoCallback(AudioCodesSession audioCodesSession) {
            Log.d(TAG, "reinviteWithVideoCallback name: " + audioCodesSession.getRemoteNumber().getDisplayName() + " userName: " + audioCodesSession.getRemoteNumber().getUserName());
            Log.d(TAG, "hasVideo: " + audioCodesSession.hasVideo());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //audioCodesSession.answer(null, true);
                    try {
                        //audioCodesSession.showVideo(CallActivity.this);
                        updateCallGui();
                    } catch (Exception e) {
                        Log.e(TAG, "error: " + e);
                    }
                }
            });
        }

        @Override
        public void mediaFailed(AudioCodesSession session) {
            Log.d(TAG, "media failed2");
            if (handler!=null)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CallActivity.this, "Media failure, trying to reconnect....", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void incomingNotify(NotifyEvent notifyEvent, String dtmfValue) {
            Log.d(TAG, "NotifyEvent: "+notifyEvent);
            onNotifyEvent(notifyEvent, dtmfValue);
        }
    }

    private void handleCallTermination(AudioCodesSession audioCodesSession, final TerminationInfo terminationInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Checking for termination: " + audioCodesSession.getSessionID());
                Toast.makeText(CallActivity.this,  "Call terminated: " + audioCodesSession.getTermination(), Toast.LENGTH_SHORT).show();
                AppUtils.stopRingingMP();
                sessionList.remove((Integer) audioCodesSession.getSessionID());
                Log.d(TAG, "sessionList.size() " +  sessionList.size());
                AudioCodesSession currentSession = session;
                if (sessionList.size() == 0) {
                    WebRTCAudioManager.getInstance().setWebRTcAudioRouteListener(null);
                    finish();
                } else {
                    switchCallButton.setEnabled(false);
                    for (Integer session1:sessionList )
                    {
                        Log.d(TAG, "session1: id " +  session1);
                    }
                    try {
                        session = AudioCodesUA.getInstance().getSession((Integer) sessionList.get(0));
                    } catch (Exception e) {
                        Log.d(TAG, "oops: " + e.getMessage());
                    }
                    if (session == null)
                    {
                        session = AudioCodesUA.getInstance().getSession(0);
                    }
                    if(audioCodesSession.getTermination() != CallTermination.TERMINATED_TRANSFER){
                        if (currentSession.getSessionID() == audioCodesSession.getSessionID()
                                &&    currentSession.getTransferState() == CallTransferState.NONE) {
                            session.hold(false);

                        }
                    }
                    updateUI();
                }
            }
        });


    }

    private class MyWebRTcAudioRouteListener implements WebRTCAudioRouteListener {

        @Override
        public void audioRoutesChanged(List<WebRTCAudioManager.AudioRoute> audioRouteList) {
            Log.d(TAG, "ROUTS: " + audioRouteList);
        }

        @Override
        public void currentAudioRouteChanged(WebRTCAudioManager.AudioRoute newAudioRoute) {
            Log.d(TAG, "currentAudioRouteChanged: " + newAudioRoute);
            route = newAudioRoute;
            if(audioRouteButton != null){
                int audioRouteInt = 0;
                if (route == WebRTCAudioManager.AudioRoute.SPEAKER_PHONE) {
                    audioRouteInt = R.drawable.call_button_icon_speaker;
                } else if (route == WebRTCAudioManager.AudioRoute.EARPIECE) {
                    audioRouteInt = R.drawable.call_button_icon_earpiece;
                } else {
                    audioRouteInt = R.drawable.call_button_icon_bluetooth;
                }
                ((ImageView) audioRouteButton).setImageResource(audioRouteInt);
            }
        }
    }
}
