package com.audiocodes.mv.webrtcclient.Activities;

import android.os.Bundle;

import android.widget.TextView;

import com.audiocodes.mv.webrtcclient.General.Log;
import com.audiocodes.mv.webrtcclient.General.Prefs;
import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcsdk.session.ACCallStatistics;



public class CallStatsActivity extends BaseAppCompatActivity {

    private static final String TAG = "CallStatsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.call_stats_activity);
        initGui();

    }

    private void initGui() {
        TextView packetsReceivedTextView =(TextView) findViewById(R.id.call_stats_textview_packets_received_text);
        TextView bytesReceivedTextView =(TextView) findViewById(R.id.call_stats_textview_bytes_received_text);
        TextView packetsLostTextView =(TextView) findViewById(R.id.call_stats_textview_packets_lost_text);
        TextView jitterTextView =(TextView) findViewById(R.id.call_stats_textview_jitter_text);
        TextView fractionLostTextView =(TextView) findViewById(R.id.call_stats_textview_fraction_lost_text);
        TextView packetsSentTextView =(TextView) findViewById(R.id.call_stats_textview_packets_sent_text);
        TextView bytesSentTextView =(TextView) findViewById(R.id.call_stats_textview_bytes_sent_text);

        ACCallStatistics acCallStatistics = Prefs.getCallStats();
        Log.d(TAG, "ACCallStatistics: " + acCallStatistics);
        try {
            ACCallStatistics.RTPInboundStatistics rtpInboundStatistics = acCallStatistics.rtpInboundStats;
            ACCallStatistics.RTPOutboundStatistics rtpOutboundStatistics = acCallStatistics.rtpOutboundStats;


            packetsReceivedTextView.setText(String.valueOf(rtpInboundStatistics.packetsReceived));
            bytesReceivedTextView.setText(String.valueOf(rtpInboundStatistics.bytesReceived));
            packetsLostTextView.setText(String.valueOf(rtpInboundStatistics.packetsLost));
            jitterTextView.setText(String.valueOf(rtpInboundStatistics.jitter));
            fractionLostTextView.setText(String.valueOf(rtpInboundStatistics.fractionLost));
            packetsSentTextView.setText(String.valueOf(rtpOutboundStatistics.packetsSent));
            bytesSentTextView.setText(String.valueOf(rtpOutboundStatistics.bytesSent));
        } catch (Exception e) {
            Log.e(TAG,"error: "+e);
        }
    }
}
