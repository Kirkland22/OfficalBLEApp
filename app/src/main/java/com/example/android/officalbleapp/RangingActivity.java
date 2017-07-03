package com.example.android.officalbleapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RangedBeacon;

import java.util.Collection;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    RelativeLayout mScreen;
    TextView mText;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    // changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        mScreen = (RelativeLayout) findViewById(R.id.myScreen);
        mText = (TextView) findViewById(R.id.textRangeView);
        RangedBeacon.setSampleExpirationMilliseconds(500);

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
                        //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");

                        if( firstBeacon.getDistance() < 1  ) {
                                changeToGreen();
                        }

                        else {
                                changeToRed();
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
                textView.setText(line+"\n");
            }
        });
    }

    /* Creates a notification that displays whenever it detects a nearby beacon */
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

    // Changes the background and text of this Activity to Red
    private void changeToRed() {
        RangingActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mScreen.setBackgroundColor(0xffff0000);
                mText.setBackgroundColor(0xffff0000);
                logToDisplay("LOCKED");
    }
});
    }
    // Changes the background and text of this Activity to Green
        private void changeToGreen() {
            RangingActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mScreen.setBackgroundColor(0xff00ff00);
                    mText.setBackgroundColor(0xff00ff00);
                    logToDisplay("UNLOCKED");
                }
            });

    }


    }


