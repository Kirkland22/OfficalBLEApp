package com.example.android.officalbleapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RangedBeacon;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private static final int AT_DOOR = 0;
    private static final int CLOSE = 1;
    private static final int FAR = 2;
    private boolean hasNeverSentRequest = true;
    RequestQueue queue;
    RelativeLayout mScreen;
    TextView mText;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    Customer customer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        queue = Volley.newRequestQueue(this);
        getSerializedObject();

        mScreen = (RelativeLayout) findViewById(R.id.myScreen);
        mText = (TextView) findViewById(R.id.textRangeView);

        beaconManager.bind(this);

        /*Changes the sampling rate of Beacon:
        Faster Sampling = less accurate measurement
        Slower Sampling = more accurate measurement
         */
        RangedBeacon.setSampleExpirationMilliseconds(500);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        changeToRed();
        //Log.e("idk","starting");

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.i("Beacon ID3", "Beacon Minor: " + firstBeacon.getId3());
                    if(firstBeacon.getId3().toInt() == 3){
                        //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + (int)firstBeacon.getDistance() + " meters away.");
                        int distance = (int)firstBeacon.getDistance();
                        //logToDisplay(((Integer)distance).toString());

                        if (distance < 1 && hasNeverSentRequest == true) {
                            changeToGreen();
                            postData(customer.getCustomerName(),customer.getAccountBalance(),customer.getLanguage());
                            hasNeverSentRequest = false;
                        }

                        else if (distance < 1 && hasNeverSentRequest == false) {

                            changeToGreen();
                        }

                        else
                            changeToRed();

                    }

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("44444444-4444-4444-4444-44444444BEAC", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView textView = (TextView) RangingActivity.this.findViewById(R.id.textRangeView);
                textView.setText(line);
            }
        });
    }

    /* Creates a notification that displays whenever it detects a nearby beacon
    private void createNotification(final String line){
        final android.content.Context _this = this;
        runOnUiThread(new Runnable() {
            public void run() {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_this);
                mBuilder.setSmallIcon(R.drawable.ic_launcher);
                mBuilder.setContentTitle("Welcome to Chase!");
                mBuilder.setContentText(line);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(10, mBuilder.build());
            }
        });
    }
    */

    // Changes the background and text of this Activity to Red while changing the screen text to "Locked"
    private void changeToRed() {
        RangingActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mScreen.setBackgroundColor(0xffff0000);
                mText.setBackgroundColor(0xffff0000);
                logToDisplay("Locked");
    }
});
    }

    // Changes the background and text of this Activity to Green while changing the screen text to "Unlocked"
    private void changeToGreen() {
            RangingActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mScreen.setBackgroundColor(0xff00ff00);
                    mText.setBackgroundColor(0xff00ff00);
                    logToDisplay("Unlocked");
                }
            });

    }

    //Sends the name to server for personalized greetings. Server: /beaconInfo
    private void postData(final String name,final String balance,final String languages) {
        StringRequest sr = new StringRequest(Request.Method.POST,"http://beaconapp-abdallahozaifa.c9users.io:8080/promotion", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("balance", balance);
                params.put("languages", languages);
                //params.put("rank", integer.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
        sr.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    // Receiving the serialized object passed in from previous intent.
    private void getSerializedObject() {
        Bundle b = this.getIntent().getExtras();
        if (b != null)
            customer = (Customer)b.getSerializable("Customer");

    }

    }




