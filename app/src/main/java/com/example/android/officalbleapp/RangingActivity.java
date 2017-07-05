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
    private int prevState;
    private int currentState = 3;
    RequestQueue queue;
    RelativeLayout mScreen;
    TextView mText;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    Customer customer;

    // changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        queue = Volley.newRequestQueue(this);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            customer = (Customer)b.getSerializable("Customer");
        mScreen = (RelativeLayout) findViewById(R.id.myScreen);
        mText = (TextView) findViewById(R.id.textRangeView);
        RangedBeacon.setSampleExpirationMilliseconds(500);
        BeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);

        beaconManager.bind(this);
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
                        //logToDisplay("Starting");
                        switch (distance) {
                            //state 0
                            case 0:
                                if (prevState == 1) {
                                    changeToGreen(distance);
                                    postData(customer.getCustomerName(), AT_DOOR);
                                    prevState = 0;
                                    break;
                                }
                                break;

                            //State 1
                            case 1:
                                if (currentState == 2) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 1;
                                    currentState = 1;
                                }
                                break;
                            //State 1
                            case 2:
                                if (currentState == 2) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 1;
                                    currentState = 1;
                                }
                                break;

                            //State 1
                            case 3:
                                if (currentState == 2) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 1;
                                    currentState = 1;
                                }
                                break;

                            case 4:
                                if (currentState == 2) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 1;
                                    currentState = 1;
                                }
                                break;

                            case 5:
                                if (currentState == 3) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 3;
                                    currentState =2;
                                }
                                break;

                            case 6:
                                if (currentState == 3) {
                                    changeToRed(distance);
                                    postData(customer.getCustomerName(), CLOSE);
                                    prevState = 3;
                                    currentState = 2;
                                }
                                break;

                            default:
                                logToDisplay("Still looking");

                        }
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

    // Changes the background and text of this Activity to Red
    private void changeToRed(final Integer distance) {
        RangingActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mScreen.setBackgroundColor(0xffff0000);
                mText.setBackgroundColor(0xffff0000);
                logToDisplay(distance.toString());
    }
});
    }
    // Changes the background and text of this Activity to Green
        private void changeToGreen(final Integer distance) {
            RangingActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mScreen.setBackgroundColor(0xff00ff00);
                    mText.setBackgroundColor(0xff00ff00);
                    logToDisplay(distance.toString());
                }
            });

    }

    public void postData(final String name, final Integer integer) {
        StringRequest sr = new StringRequest(Request.Method.POST,"http://beaconapp-abdallahozaifa.c9users.io:8080/beaconInfo", new Response.Listener<String>() {
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
                params.put("rank", integer.toString());
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
    }

    }


